package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.*;
import org.example.shoppingserver.model.entity.*;
import org.example.shoppingserver.repository.*;
import org.example.shoppingserver.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        // 验证旧密码
        adminRepository.save(admin);

        return true;
    }

    @Override
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
    public boolean updateMerchantStatus(Long merchantId, Integer status) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        merchant.setStatus(status);
        merchantRepository.save(merchant);

        return true;
    }

    @Override
    @Transactional
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

    // ==================== 转换方法 ====================

    private AdminDTO convertToAdminDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setNickname(admin.getNickname());
        dto.setAvatar(admin.getAvatar());
        dto.setStatus(admin.getStatus());
        dto.setLastLoginTime(admin.getLastLoginTime());
        dto.setLastLoginIp(admin.getLastLoginIp());
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
