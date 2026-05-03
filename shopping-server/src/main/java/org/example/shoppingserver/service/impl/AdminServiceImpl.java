package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.*;
import org.example.shoppingserver.model.entity.*;
import org.example.shoppingserver.model.vo.CategoryVO;
import org.example.shoppingserver.model.vo.ProductVO;
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


    @Override
    public AdminDTO login(String username, String password) {
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

        return convertToAdminDTO(admin);
    }

    @Override
    @Cacheable(value = "adminInfo", key = "#adminId", unless = "#result == null")
    public AdminDTO getCurrentAdmin() {
        String adminId = UserHolder.getCurrentUserId();
        if (adminId == null) {
            throw new RuntimeException("未登录");
        }

        Admin admin = adminRepository.findById(Long.parseLong(adminId))
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        return convertToAdminDTO(admin);
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
    public PlatformStatisticsDTO getPlatformStatistics() {
        PlatformStatisticsDTO statistics = new PlatformStatisticsDTO();

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
    public Page<MerchantApplicationDTO> getPendingApplications(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<MerchantApplication> applications = merchantApplicationRepository
                .findByStatus(AuditStatus.PENDING, pageable);
        return applications.map(this::convertToApplicationDTO);
    }

    @Override
    public Page<MerchantApplicationDTO> getAllApplications(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<MerchantApplication> applications = merchantApplicationRepository.findAll(pageable);
        return applications.map(this::convertToApplicationDTO);
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
        }

        return true;
    }

    // ==================== 商家管理 ====================

    @Override
    public Page<MerchantDTO> getAllMerchants(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findAll(pageable);
        return merchants.map(this::convertToMerchantDTO);
    }

    @Override
    public Page<MerchantDTO> getMerchantsByStatus(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findByStatus(status, pageable);
        return merchants.map(this::convertToMerchantDTO);
    }

    @Override
    public Page<MerchantApplicationDTO> getMerchantsByAuditStatus(Integer auditStatus, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        AuditStatus status = AuditStatus.fromCode(auditStatus);
        if (status == null) {
            throw new RuntimeException("无效的审核状态");
        }
        Page<MerchantApplication> merchants = merchantApplicationRepository.findByStatus(status, pageable);
        return merchants.map(this::convertToApplicationDTO);
    }

    @Override
    public MerchantDTO getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        return convertToMerchantDTO(merchant);
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
    public List<MerchantGroupDTO> getMerchantsGroupedByCategory() {
        // 获取所有正常状态的商家
        List<Merchant> merchants = merchantRepository.findByStatus(1,
                PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        // 按主营类目分组
        Map<String, List<Merchant>> groupedByCategory = merchants.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategory().toString() != null ? m.getCategory().toString() : "其他"
                ));
        List<MerchantGroupDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Merchant>> entry : groupedByCategory.entrySet()) {
            MerchantGroupDTO groupDTO = new MerchantGroupDTO();
            groupDTO.setCategoryName(entry.getKey());
            groupDTO.setCategoryDescription(getCategoryDescription(entry.getKey()));
            groupDTO.setMerchantCount((long) entry.getValue().size());
            groupDTO.setMerchants(entry.getValue().stream()
                    .map(this::convertToMerchantDTO)
                    .collect(Collectors.toList()));
            result.add(groupDTO);
        }

        // 按商家数量排序
        result.sort((a, b) -> b.getMerchantCount().compareTo(a.getMerchantCount()));

        return result;
    }

    @Override
    public Page<MerchantDTO> getMerchantsByCategory(String category, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Merchant> merchants = merchantRepository.findByCategory(category, pageable);
        return merchants.map(this::convertToMerchantDTO);
    }

    // ==================== 用户管理 ====================

    @Override
    public Page<UserDTO> getUserList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserDTO);
    }

    @Override
    public Page<UserDTO> getUsersByStatus(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return users.map(this::convertToUserDTO);
    }

    @Override
    public UserDTO getUserDetail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToUserDTO(user);
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
    public Page<OrderDTO> getOrderList(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> orders;
        if (status == null) {
            orders = orderRepository.findAll(pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.fromCode(status);
            orders = orderRepository.findAllByStatus(orderStatus, pageable);
        }
        return orders.map(this::convertToOrderDTO);
    }

    @Override
    public OrderDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToOrderDTO(order);
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
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToProductVO);
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
    public Page<WithdrawRecordDTO> getWithdrawRecords(Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<WithdrawRecord> records;
        if (status == null) {
            records = withdrawRecordRepository.findAll(pageable);
        } else {
            records = withdrawRecordRepository.findByStatus(status, pageable);
        }
        return records.map(this::convertToWithdrawRecordDTO);
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
    public FinanceStatistics getFinanceStatistics() {
        FinanceStatistics statistics = new FinanceStatistics();
        
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
    public List<BannerDTO> getBanners() {
        List<Banner> banners = bannerRepository.findAll();
        return banners.stream().map(this::convertToBannerDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Long createBanner(BannerDTO dto) {
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
    public void updateBanner(Long bannerId, BannerDTO dto) {
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
    public List<AnnouncementDTO> getAnnouncements(Integer type) {
        List<Announcement> announcements;
        if (type == null) {
            announcements = announcementRepository.findAll();
        } else {
            announcements = announcementRepository.findByType(type);
        }
        return announcements.stream().map(this::convertToAnnouncementDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Long createAnnouncement(AnnouncementDTO dto) {
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
    public void updateAnnouncement(Long announcementId, AnnouncementDTO dto) {
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

    private AdminDTO convertToAdminDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setStatus(admin.getStatus());
        dto.setLastLoginTime(admin.getLastLoginTime());
        return dto;
    }

    // TODO: 待启用用户管理功能后使用

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setBirthday(user.getBirthday());
        dto.setStatus(user.getStatus());
        dto.setBalance(user.getBalance());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUser().getId());
        dto.setMerchantId(order.getMerchant().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setStatus(order.getStatus().getCode());
        dto.setStatusDescription(order.getStatus().getDescription());
        dto.setPayTime(order.getPayTime());
        dto.setDeliveryTime(order.getShipTime());
        dto.setReceiveTime(order.getReceiveTime());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
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

    private WithdrawRecordDTO convertToWithdrawRecordDTO(WithdrawRecord record) {
        WithdrawRecordDTO dto = new WithdrawRecordDTO();
        dto.setId(record.getId());
        dto.setMerchantId(record.getMerchant().getId());
        dto.setMerchantName(record.getMerchant().getStoreName());
        dto.setAmount(record.getAmount());
        dto.setFee(record.getFee());
        dto.setActualAmount(record.getActualAmount());
        dto.setAccount(record.getAccountName());
        dto.setBankName(record.getBankName());
        dto.setStatus(record.getStatus());
        dto.setReason(record.getAuditReason());
        dto.setApplyTime(record.getApplyTime());
        dto.setAuditTime(record.getAuditTime());
        dto.setTransferTime(record.getTransferTime());
        return dto;
    }

    private BannerDTO convertToBannerDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        dto.setImage(banner.getImage());
        dto.setImageUrl(banner.getImage()); // 兼容字段
        dto.setLink(banner.getLink());
        dto.setLinkUrl(banner.getLink()); // 兼容字段
        dto.setPosition(banner.getPosition());
        dto.setSort(banner.getSort());
        dto.setStatus(banner.getStatus());
        dto.setClickCount(banner.getClickCount());
        dto.setStartTime(banner.getStartTime());
        dto.setEndTime(banner.getEndTime());
        dto.setCreatedAt(banner.getCreatedAt());
        return dto;
    }

    private AnnouncementDTO convertToAnnouncementDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setType(announcement.getType());
        dto.setStatus(announcement.getStatus());
        dto.setPublishTime(announcement.getPublishTime());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }


    private MerchantApplicationDTO convertToApplicationDTO(MerchantApplication application) {
        MerchantApplicationDTO dto = new MerchantApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUser().getId());
        dto.setUsername(application.getUser().getUsername());
        dto.setStoreName(application.getStoreName());
        dto.setStoreType(application.getStoreType());
        dto.setCategory(application.getCategory());
        dto.setContactName(application.getContactName());
        dto.setContactPhone(application.getContactPhone());
        dto.setContactEmail(application.getContactEmail());
        dto.setBusinessLicense(application.getBusinessLicense());
        dto.setIdCardFront(application.getIdCardFront());
        dto.setIdCardBack(application.getIdCardBack());
        dto.setStatus(application.getStatus().getCode());
        dto.setStatusDescription(application.getStatus().getDescription());
        dto.setRemark(application.getRemark());
        dto.setApplyTime(application.getApplyTime());
        dto.setAuditTime(application.getAuditTime());
        return dto;
    }

    private MerchantDTO convertToMerchantDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setUserId(merchant.getUserId());
        dto.setStoreName(merchant.getStoreName());
        dto.setStoreLogo(merchant.getStoreLogo());
        dto.setStoreBanner(merchant.getStoreBanner());
        dto.setStoreDescription(merchant.getStoreDescription());
        dto.setStoreType(merchant.getStoreType());
        dto.setCategory(merchant.getCategory());
        dto.setContactName(merchant.getContactName());
        dto.setContactPhone(merchant.getContactPhone());
        dto.setContactEmail(merchant.getContactEmail());
        dto.setRating(merchant.getRating());
        dto.setSales(merchant.getSales());
        dto.setFollowers(merchant.getFollowers());
        dto.setStatus(merchant.getStatus());
        dto.setAuditStatus(merchant.getAuditStatus().getCode());
        return dto;
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
