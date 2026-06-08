package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonapi.dto.product.ProductSimpleVO;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.merchant.MerchantAuditDTO;
import org.example.shoppingserver.model.dto.product.CategoryDTO;
import org.example.shoppingserver.model.vo.admin.AdminVO;
import org.example.shoppingserver.model.vo.admin.PlatformStatisticsVO;
import org.example.shoppingserver.model.vo.finance.FinanceStatisticsVO;
import org.example.shoppingserver.model.vo.finance.WithdrawRecordVO;
import org.example.shoppingserver.model.vo.marketing.AnnouncementVO;
import org.example.shoppingserver.model.vo.marketing.BannerVO;
import org.example.shoppingserver.model.vo.merchant.MerchantApplicationVO;
import org.example.shoppingserver.model.vo.merchant.MerchantGroupVO;
import org.example.shoppingserver.model.vo.merchant.MerchantVO;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.user.UserVO;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.entity.admin.Admin;
import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.common.Category;
import org.example.shoppingserver.model.entity.finance.WithdrawRecord;
import org.example.shoppingserver.model.entity.marketing.Announcement;
import org.example.shoppingserver.model.entity.marketing.Banner;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.model.entity.merchant.MerchantApplication;
import org.example.shoppingserver.model.entity.order.Order;
import org.example.shoppingserver.model.entity.order.OrderStatus;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.vo.product.CategoryVO;
import org.example.shoppingserver.model.vo.product.ProductVO;
import org.example.shoppingserver.mq.producer.MerchantProducer;
import org.example.shoppingserver.repository.*;
import org.example.shoppingserver.service.AdminService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 *
 * @author System
 * @since 2026-04-28
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final MerchantApplicationRepository merchantApplicationRepository;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WithdrawRecordRepository withdrawRecordRepository;
    private final BannerRepository bannerRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MerchantProducer merchantProducer;


    @Override
    public AdminVO login(String username, String password) {
        // 查询管理员
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 检查状态
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新最后登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        adminRepository.save(admin);

        return convertToAdminVO(admin);
    }

    @Override
    @Cacheable(value = "adminInfo", key = "#adminId", unless = "#result == null")
    public AdminVO getCurrentAdmin() {
        String adminId = UserHolder.getCurrentUserId();
        if (adminId == null) {
            throw new RuntimeException("未登录");
        }

        Admin admin = adminRepository.findById(Long.parseLong(adminId))
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        return convertToAdminVO(admin);
    }

    @Override
    @Transactional
    @CacheEvict(value = "adminInfo", allEntries = true)
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 设置新密码
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);

        return true;
    }

    @Override
    @Transactional
    public Long createAdmin(String username, String password) {
        // 检查用户名是否已存在
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新管理员
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setStatus(1);
        
        Admin savedAdmin = adminRepository.save(admin);
        return savedAdmin.getId();
    }

    @Override
    @CacheEvict(value = "adminInfo", allEntries = true)
    public void logout() {
        // 清除缓存，实现退出登录
        log.info("管理员退出登录");
    }

    @Override
    @Cacheable(value = "platformStatistics", unless = "#result == null")
    public PlatformStatisticsVO getPlatformStatistics() {
        PlatformStatisticsVO statistics = new PlatformStatisticsVO();

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 用户统计
        statistics.setTotalUsers(userRepository.count());
        statistics.setTodayUsers(userRepository.countByStatus(1)); // 简化处理

        // 商家统计
        statistics.setTotalMerchants(merchantRepository.count());
        statistics.setTodayMerchants(merchantRepository.countByStatus(1)); // 简化处理

        // 订单统计
        statistics.setTotalOrders(orderRepository.count());
        statistics.setTodayOrders(orderRepository.countTodayOrders(todayStart));

        // 销售额统计
        statistics.setTotalSales(orderRepository.findAll().stream()
                .filter(o -> o.getStatus().getCode() >= 3)
                .map(Order::getPayAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        statistics.setTodaySales(orderRepository.sumTodaySales(todayStart));

        // 商品统计
        statistics.setTotalProducts(productRepository.count());

        // 待处理退款
        statistics.setPendingRefunds(0L); // 需要 OrderRefundRepository 支持

        // 待审核商家
        statistics.setPendingAudits(merchantApplicationRepository.countByStatus(AuditStatus.PENDING));

        return statistics;
    }

    // ==================== 商家入驻申请管理 ====================

    @Override
    public Page<MerchantApplicationVO> getPendingApplications(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<MerchantApplication> applications = merchantApplicationRepository
                .findByStatus(AuditStatus.PENDING, pageable);
        return applications.map(this::convertToApplicationVO);
    }

    @Override
    public Page<MerchantApplicationVO> getAllApplications(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<MerchantApplication> applications = merchantApplicationRepository.findAll(pageable);
        return applications.map(this::convertToApplicationVO);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"pendingApplications", "allApplications"}, allEntries = true)
    public boolean auditMerchantApplication(MerchantAuditDTO auditDTO) {
        // 查询申请记录
        MerchantApplication application = merchantApplicationRepository.findById(auditDTO.getId())
                .orElseThrow(() -> new RuntimeException("申请记录不存在"));
        if (application.getStatus() != AuditStatus.PENDING) {
            throw new RuntimeException("该申请已处理，无需重复审核");
        }

        // 更新审核状态
        AuditStatus newStatus = auditDTO.getStatus() == 1 ? AuditStatus.APPROVED : AuditStatus.REJECTED;
        application.setStatus(newStatus);
        application.setRemark(auditDTO.getRemark());
        application.setAuditTime(LocalDateTime.now());
        merchantApplicationRepository.save(application);

        // 如果审核通过，创建商家记录
        if (newStatus == AuditStatus.APPROVED) {
            // 检查是否已存在商家记录
            Optional<Merchant> existingMerchant = merchantRepository.findByUserId(application.getUser().getId());
            if (existingMerchant.isEmpty()) {
                Merchant merchant = new Merchant();
                merchant.setUserId(application.getUser().getId());
                merchant.setStoreName(application.getStoreName());
                merchant.setStoreType(application.getStoreType());
                merchant.setCategory(application.getCategory());
                merchant.setContactName(application.getContactName());
                merchant.setContactPhone(application.getContactPhone());
                merchant.setContactEmail(application.getContactEmail());
                merchant.setAuditStatus(AuditStatus.APPROVED);
                merchant.setStatus(1);
                merchant.initAccount();
                merchantRepository.save(merchant);
            }
            merchantProducer.sendMerchantRegisterMessage(application.getUser().getId());
        }

        return true;
    }

    // ==================== 商家管理 ====================

    @Override
    public Page<MerchantVO> getAllMerchants(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findAll(pageable);
        return merchants.map(this::convertToMerchantVO);
    }

    @Override
    public Page<MerchantVO> getMerchantsByStatus(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findByStatus(status, pageable);
        return merchants.map(this::convertToMerchantVO);
    }

    @Override
    public Page<MerchantApplicationVO> getMerchantsByAuditStatus(Integer auditStatus, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        AuditStatus status = AuditStatus.fromCode(auditStatus);
        if (status == null) {
            throw new RuntimeException("无效的审核状态");
        }
        Page<MerchantApplication> merchants = merchantApplicationRepository.findByStatus(status, pageable);
        return merchants.map(this::convertToApplicationVO);
    }

    @Override
    public MerchantVO getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        return convertToMerchantVO(merchant);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"merchants", "merchantsByStatus", "merchantsByAuditStatus", "merchantDetail"}, allEntries = true)
    public boolean updateMerchantStatus(Long merchantId, Integer status) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        merchant.setStatus(status);
        merchantRepository.save(merchant);

        return true;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"merchants", "merchantsByStatus", "merchantsByAuditStatus", "merchantDetail"}, allEntries = true)
    public boolean deleteMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        // 检查是否有商品
        long productCount = productRepository.countByMerchantId(merchantId);
        if (productCount > 0) {
            throw new RuntimeException("该商家还有商品，无法删除");
        }

        // 检查是否有订单
        long orderCount = orderRepository.countByMerchantId(merchantId);
        if (orderCount > 0) {
            throw new RuntimeException("该商家还有订单，无法删除");
        }

        merchantRepository.delete(merchant);
        return true;
    }

    // ==================== 商家分组展示 ====================

    @Override
    public List<MerchantGroupVO> getMerchantsGroupedByCategory() {
        // 获取所有正常状态的商家
        List<Merchant> merchants = merchantRepository.findByStatus(1,
                PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        // 按主营类目分组
        Map<String, List<Merchant>> groupedByCategory = merchants.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategory().toString() != null ? m.getCategory().toString() : "其他"
                ));
        List<MerchantGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<Merchant>> entry : groupedByCategory.entrySet()) {
            MerchantGroupVO groupVO = new MerchantGroupVO();
            groupVO.setCategoryName(entry.getKey());
            groupVO.setCategoryDescription(getCategoryDescription(entry.getKey()));
            groupVO.setMerchantCount((long) entry.getValue().size());
            groupVO.setMerchants(entry.getValue().stream()
                    .map(this::convertToMerchantVO)
                    .collect(Collectors.toList()));
            result.add(groupVO);
        }

        // 按商家数量排序
        result.sort((a, b) -> b.getMerchantCount().compareTo(a.getMerchantCount()));

        return result;
    }

    @Override
    public Page<MerchantVO> getMerchantsByCategory(String category, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findByCategory(category, pageable);
        return merchants.map(this::convertToMerchantVO);
    }

    // ==================== 用户管理 ====================

    @Override
    public Page<UserVO> getUserList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserVO);
    }

    @Override
    public Page<UserVO> getUsersByStatus(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return users.map(this::convertToUserVO);
    }

    @Override
    public UserVO getUserDetail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToUserVO(user);
    }

    @Override
    @Transactional
    public boolean updateUserStatus(String userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        userRepository.delete(user);
        return true;
    }

    // ==================== 订单管理 ====================

    @Override
    public Page<OrderVO> getOrderList(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> orders;
        if (status == null) {
            orders = orderRepository.findAll(pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.fromCode(status);
            orders = orderRepository.findAllByStatus(orderStatus, pageable);
        }
        return orders.map(this::convertToOrderVO);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToOrderVO(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.cancel();
        orderRepository.save(order);
        return true;
    }

    @Override
    @Transactional
    public boolean forceCompleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.confirmReceipt();
        orderRepository.save(order);
        return true;
    }

    // ==================== 商品审核 ====================

    @Override
    public Page<ProductVO> getPendingProducts(int pageNum, int pageSize) {
        // Product 实体暂无审核状态字段，返回所有下架商品
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> products = productRepository.findAllByAuditStatus(AuditStatus.PENDING, pageable);
        return products.map(this::convertToProductVO);
    }

    @Override
    public List<ProductVO> getAIPendingProducts(int pageNum, int pageSize) {
        // Product 实体暂无审核状态字段，返回所有下架商品
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        List<Product> products = productRepository.findByAuditStatus(AuditStatus.PENDING, pageable);
        return products.stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取待审核商品列表（简化版）
     * 用于微服务间调用，返回简化版商品信息
     */
    private ProductSimpleVO convertToProductSimpleVO(Product product) {
        ProductSimpleVO vo = new ProductSimpleVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubName(product.getSubName());
        vo.setImage(product.getImage());
        vo.setImages(product.getImages());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setSoldCount(product.getSoldCount());
        vo.setMerchantId(product.getMerchant() != null ? product.getMerchant().getId() : null);
        vo.setCategoryId(product.getCategoryId());
        vo.setPublishStatus(product.getPublishStatus());
        vo.setAuditStatus(product.getAuditStatus() != null ? product.getAuditStatus().getCode() : null);
        vo.setCreateTime(product.getCreatedAt());
        return vo;
    }

    @Override
    public Page<ProductVO> getAllProducts(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToProductVO);
    }

    @Override
    public ProductVO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return convertToProductVO(product);
    }

    @Override
    @Transactional
    public boolean auditProduct(Long productId, Boolean approved, String reason) {
        // Product 实体暂无审核字段，这里仅做下架/上架处理
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // approved=true 则上架，否则下架
        product.setAuditStatus(approved ? AuditStatus.APPROVED : AuditStatus.REJECTED);
        product.setPublishStatus(approved ? 1 : 0);
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean offlineProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setPublishStatus(0); // 下架
        productRepository.save(product);
        return true;
    }

    // ==================== 分类管理 ====================

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> topCategories = categoryRepository.findTopCategories();
        return topCategories.stream()
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setLevel(dto.getLevel());
        category.setParentId(dto.getParentId());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        return categoryRepository.save(category).getId();
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        category.setName(dto.getName());
        category.setLevel(dto.getLevel());
        category.setParentId(dto.getParentId());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort());
        category.setStatus(dto.getStatus());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查是否有子分类
        long childCount = categoryRepository.countByParentId(categoryId);
        if (childCount > 0) {
            throw new RuntimeException("该分类下还有子分类，无法删除");
        }
        
        categoryRepository.delete(category);
    }

    // ==================== 财务管理 ====================

    @Override
    public Page<WithdrawRecordVO> getWithdrawRecords(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<WithdrawRecord> records;
        if (status == null) {
            records = withdrawRecordRepository.findAll(pageable);
        } else {
            records = withdrawRecordRepository.findByStatus(status, pageable);
        }
        return records.map(this::convertToWithdrawRecordVO);
    }

    @Override
    @Transactional
    public boolean auditWithdraw(Long withdrawId, Boolean approved, String reason) {
        WithdrawRecord record = withdrawRecordRepository.findById(withdrawId)
                .orElseThrow(() -> new RuntimeException("提现记录不存在"));
        
        if (approved) {
            record.approve(); // 审核通过
        } else {
            record.reject(reason); // 拒绝
        }
        withdrawRecordRepository.save(record);
        return true;
    }

    @Override
    public FinanceStatisticsVO getFinanceStatistics() {
        FinanceStatisticsVO statistics = new FinanceStatisticsVO();
        
        // 总提现金额（已打款）
        statistics.setTotalWithdraw(
            withdrawRecordRepository.findAll().stream()
                .filter(r -> r.getStatus() == 2)
                .map(WithdrawRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        // 待审核提现
        statistics.setPendingWithdraw(
            withdrawRecordRepository.findAll().stream()
                .filter(r -> r.getStatus() == 0)
                .map(WithdrawRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        // 已完成提现（同总提现）
        statistics.setCompletedWithdraw(statistics.getTotalWithdraw());
        
        // 提现次数统计
        statistics.setTotalWithdrawCount(withdrawRecordRepository.count());
        statistics.setPendingWithdrawCount(withdrawRecordRepository.countByStatus(0));
        
        return statistics;
    }

    // ==================== 营销管理（轮播图、公告）====================
    
    @Override
    public List<BannerVO> getBanners() {
        List<Banner> banners = bannerRepository.findAll();
        return banners.stream().map(this::convertToBannerVO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Long createBanner(BannerVO dto) {
        Banner banner = new Banner();
        banner.setTitle(dto.getTitle());
        banner.setImage(dto.getImageUrl() != null ? dto.getImageUrl() : dto.getImage());
        banner.setLink(dto.getLinkUrl() != null ? dto.getLinkUrl() : dto.getLink());
        banner.setPosition(dto.getPosition());
        banner.setSort(dto.getSort() != null ? dto.getSort() : 0);
        banner.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        return bannerRepository.save(banner).getId();
    }
    
    @Override
    @Transactional
    public void updateBanner(Long bannerId, BannerVO dto) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));
        banner.setTitle(dto.getTitle());
        banner.setImage(dto.getImageUrl() != null ? dto.getImageUrl() : dto.getImage());
        banner.setLink(dto.getLinkUrl() != null ? dto.getLinkUrl() : dto.getLink());
        banner.setPosition(dto.getPosition());
        banner.setSort(dto.getSort());
        banner.setStatus(dto.getStatus());
        bannerRepository.save(banner);
    }
    
    @Override
    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));
        bannerRepository.delete(banner);
    }
    
    @Override
    @Transactional
    public void updateBannerStatus(Long bannerId, Integer status) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("轮播图不存在"));
        banner.setStatus(status);
        bannerRepository.save(banner);
    }
    
    @Override
    public List<AnnouncementVO> getAnnouncements(Integer type) {
        List<Announcement> announcements;
        if (type == null) {
            announcements = announcementRepository.findAll();
        } else {
            announcements = announcementRepository.findByType(type);
        }
        return announcements.stream().map(this::convertToAnnouncementVO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Long createAnnouncement(AnnouncementVO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType());
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        announcement.setPublishTime(dto.getPublishTime() != null ? dto.getPublishTime() : LocalDateTime.now());
        return announcementRepository.save(announcement).getId();
    }
    
    @Override
    @Transactional
    public void updateAnnouncement(Long announcementId, AnnouncementVO dto) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType());
        announcement.setStatus(dto.getStatus());
        announcementRepository.save(announcement);
    }
    
    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcementRepository.delete(announcement);
    }
    
    @Override
    @Transactional
    public void updateAnnouncementStatus(Long announcementId, Integer status) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcement.setStatus(status);
        announcementRepository.save(announcement);
    }

    // ==================== 转换方法 ====================

    private AdminVO convertToAdminVO(Admin admin) {
        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setStatus(admin.getStatus());
        vo.setLastLoginTime(admin.getLastLoginTime());
        return vo;
    }

    // TODO: 待启用用户管理功能后使用

    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setStatus(user.getStatus());
        vo.setBalance(user.getBalance());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }

    private OrderVO convertToOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUser().getId());
        vo.setMerchantId(order.getMerchant().getId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus().getCode());
        vo.setStatusDescription(order.getStatus().getDescription());
        vo.setPayTime(order.getPayTime());
        vo.setShipTime(order.getShipTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setCreatedAt(order.getCreatedAt());
        return vo;
    }

    private ProductVO convertToProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setMainImage(product.getImage());
        vo.setImages(product.getImages());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSoldCount());
        vo.setStatus(product.getPublishStatus());
        vo.setAuditStatus(product.getAuditStatus() != null ? product.getAuditStatus().getCode() : null);
        vo.setCategoryId(product.getCategoryId());
        vo.setMerchantId(product.getMerchant() != null ? product.getMerchant().getId() : null);
        vo.setCreatedAt(product.getCreatedAt());
        return vo;
    }

    private CategoryVO buildCategoryTree(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setLevel(category.getLevel());
        vo.setParentId(category.getParentId());
        vo.setIcon(category.getIcon());
        vo.setSort(category.getSort());
        vo.setStatus(category.getStatus());
            
        // 递归获取子分类
        List<Category> children = categoryRepository.findByParentId(category.getId());
        if (children != null && !children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(this::buildCategoryTree)
                    .collect(Collectors.toList()));
        }
            
        return vo;
    }

    private WithdrawRecordVO convertToWithdrawRecordVO(WithdrawRecord record) {
        WithdrawRecordVO vo = new WithdrawRecordVO();
        vo.setId(record.getId());
        vo.setMerchantId(record.getMerchant().getId());
        vo.setMerchantName(record.getMerchant().getStoreName());
        vo.setAmount(record.getAmount());
        vo.setFee(record.getFee());
        vo.setActualAmount(record.getActualAmount());
        vo.setAccount(record.getAccountName());
        vo.setBankName(record.getBankName());
        vo.setStatus(record.getStatus());
        vo.setReason(record.getAuditReason());
        vo.setApplyTime(record.getApplyTime());
        vo.setAuditTime(record.getAuditTime());
        vo.setTransferTime(record.getTransferTime());
        return vo;
    }

    private BannerVO convertToBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setImage(banner.getImage());
        vo.setImageUrl(banner.getImage()); // 兼容字段
        vo.setLink(banner.getLink());
        vo.setLinkUrl(banner.getLink()); // 兼容字段
        vo.setPosition(banner.getPosition());
        vo.setSort(banner.getSort());
        vo.setStatus(banner.getStatus());
        vo.setClickCount(banner.getClickCount());
        vo.setStartTime(banner.getStartTime());
        vo.setEndTime(banner.getEndTime());
        vo.setCreatedAt(banner.getCreatedAt());
        return vo;
    }

    private AnnouncementVO convertToAnnouncementVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.getId());
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setType(announcement.getType());
        vo.setStatus(announcement.getStatus());
        vo.setPublishTime(announcement.getPublishTime());
        vo.setCreatedAt(announcement.getCreatedAt());
        return vo;
    }


    private MerchantApplicationVO convertToApplicationVO(MerchantApplication application) {
        MerchantApplicationVO vo = new MerchantApplicationVO();
        vo.setId(application.getId());
        vo.setUserId(application.getUser().getId());
        vo.setUsername(application.getUser().getUsername());
        vo.setStoreName(application.getStoreName());
        vo.setStoreType(application.getStoreType());
        vo.setCategory(application.getCategory());
        vo.setContactName(application.getContactName());
        vo.setContactPhone(application.getContactPhone());
        vo.setContactEmail(application.getContactEmail());
        vo.setBusinessLicense(application.getBusinessLicense());
        vo.setIdCardFront(application.getIdCardFront());
        vo.setIdCardBack(application.getIdCardBack());
        vo.setStatus(application.getStatus().getCode());
        vo.setStatusDescription(application.getStatus().getDescription());
        vo.setRemark(application.getRemark());
        vo.setApplyTime(application.getApplyTime());
        vo.setAuditTime(application.getAuditTime());
        return vo;
    }

    private MerchantVO convertToMerchantVO(Merchant merchant) {
        MerchantVO vo = new MerchantVO();
        vo.setId(merchant.getId());
        vo.setUserId(merchant.getUserId());
        vo.setStoreName(merchant.getStoreName());
        vo.setStoreLogo(merchant.getStoreLogo());
        vo.setStoreBanner(merchant.getStoreBanner());
        vo.setStoreDescription(merchant.getStoreDescription());
        vo.setStoreType(merchant.getStoreType());
        vo.setCategory(merchant.getCategory());
        vo.setContactName(merchant.getContactName());
        vo.setContactPhone(merchant.getContactPhone());
        vo.setContactEmail(merchant.getContactEmail());
        vo.setRating(merchant.getRating());
        vo.setSales(merchant.getSales());
        vo.setFollowers(merchant.getFollowers());
        vo.setStatus(merchant.getStatus());
        vo.setAuditStatus(merchant.getAuditStatus().getCode());
        return vo;
    }

    private String getCategoryDescription(String category) {
        Map<String, String> categoryDescriptions = new HashMap<>();
        categoryDescriptions.put("数码电子", "手机、电脑、数码配件等电子产品");
        categoryDescriptions.put("服饰服装", "男装、女装、鞋靴、箱包等");
        categoryDescriptions.put("家居生活", "家具、家纺、厨具、生活用品等");
        categoryDescriptions.put("美妆护肤", "化妆品、护肤品、香水等");
        categoryDescriptions.put("食品饮料", "零食、生鲜、酒类、茶叶等");
        categoryDescriptions.put("母婴用品", "奶粉、纸尿裤、玩具、童装等");
        categoryDescriptions.put("运动户外", "运动装备、户外用品、健身器材等");
        categoryDescriptions.put("图书音像", "图书、电子书、音像制品等");
        return categoryDescriptions.getOrDefault(category, "其他类目");
    }
}
