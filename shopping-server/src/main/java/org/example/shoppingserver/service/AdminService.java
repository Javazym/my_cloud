package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.*;
import org.example.shoppingserver.model.vo.CategoryVO;
import org.example.shoppingserver.model.vo.ProductVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 管理员Service接口
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息
     */
    AdminDTO login(String username, String password);

    /**
     * 获取当前登录管理员
     *
     * @return 管理员信息
     */
    AdminDTO getCurrentAdmin();

    /**
     * 修改密码
     *
     * @param adminId    管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long adminId, String oldPassword, String newPassword);

    /**
     * 创建管理员
     *
     * @param username 用户名
     * @param password 密码
     * @return 管理员ID
     */
    Long createAdmin(String username, String password);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取平台统计数据
     *
     * @return 统计数据
     */
    PlatformStatisticsDTO getPlatformStatistics();

    // ==================== 商家入驻申请管理 ====================

    /**
     * 获取待审核的商家申请列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 申请列表
     */
    Page<MerchantApplicationDTO> getPendingApplications(int pageNum, int pageSize);

    /**
     * 获取所有商家申请列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 申请列表
     */
    Page<MerchantApplicationDTO> getAllApplications(int pageNum, int pageSize);

    /**
     * 审核商家入驻申请
     *
     * @param auditDTO 审核信息
     * @return 是否成功
     */
    boolean auditMerchantApplication(MerchantAuditDTO auditDTO);

    // ==================== 商家管理 ====================

    /**
     * 获取所有商家列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商家列表
     */
    Page<MerchantDTO> getAllMerchants(int pageNum, int pageSize);

    /**
     * 根据状态获取商家列表
     *
     * @param status 状态：0-禁用，1-正常
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商家列表
     */
    Page<MerchantDTO> getMerchantsByStatus(Integer status, int pageNum, int pageSize);

    /**
     * 根据审核状态获取商家列表
     *
     * @param auditStatus 审核状态：0-待审核，1-通过，2-驳回
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商家列表
     */
    Page<MerchantApplicationDTO> getMerchantsByAuditStatus(Integer auditStatus, int pageNum, int pageSize);

    /**
     * 获取商家详情
     *
     * @param merchantId 商家ID
     * @return 商家详情
     */
    MerchantDTO getMerchantDetail(Long merchantId);

    /**
     * 更新商家状态
     *
     * @param merchantId 商家ID
     * @param status 状态：0-禁用，1-正常
     * @return 是否成功
     */
    boolean updateMerchantStatus(Long merchantId, Integer status);

    /**
     * 删除商家
     *
     * @param merchantId 商家ID
     * @return 是否成功
     */
    boolean deleteMerchant(Long merchantId);

    // ==================== 商家分组展示 ====================

    /**
     * 按主营类目分组展示商家
     *
     * @return 商家分组列表
     */
    List<MerchantGroupDTO> getMerchantsGroupedByCategory();

    /**
     * 获取指定类目的商家列表
     *
     * @param category 主营类目
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商家列表
     */
    Page<MerchantDTO> getMerchantsByCategory(String category, int pageNum, int pageSize);

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户分页列表
     */
    Page<UserDTO> getUserList(int pageNum, int pageSize);

    /**
     * 根据状态获取用户列表
     *
     * @param status 用户状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户分页列表
     */
    Page<UserDTO> getUsersByStatus(Integer status, int pageNum, int pageSize);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDTO getUserDetail(String userId);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserStatus(String userId, Integer status);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(String userId);

    // ==================== 订单管理 ====================

    /**
     * 获取订单列表
     *
     * @param status 订单状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    Page<OrderDTO> getOrderList(Integer status, int pageNum, int pageSize);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetail(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId);

    /**
     * 强制完成订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean forceCompleteOrder(Long orderId);

    // ==================== 商品审核 ====================

    /**
     * 获取待审核商品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 商品分页列表
     */
    Page<ProductVO> getPendingProducts(int pageNum, int pageSize);

    /**
     * 获取所有商品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 商品分页列表
     */
    Page<ProductVO> getAllProducts(int pageNum, int pageSize);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductVO getProductDetail(Long productId);

    /**
     * 审核商品
     *
     * @param productId 商品ID
     * @param approved 是否通过
     * @param reason 审核原因
     * @return 是否成功
     */
    boolean auditProduct(Long productId, Boolean approved, String reason);

    /**
     * 下架商品
     *
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean offlineProduct(Long productId);

    // ==================== 分类管理 ====================

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 创建分类
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    Long createCategory(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param dto 分类信息
     */
    void updateCategory(Long categoryId, CategoryDTO dto);

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);

    // ==================== 财务管理 ====================

    /**
     * 获取提现申请列表
     *
     * @param status 状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 提现申请分页列表
     */
    Page<WithdrawRecordDTO> getWithdrawRecords(Integer status, int pageNum, int pageSize);

    /**
     * 审核提现申请
     *
     * @param withdrawId 提现ID
     * @param approved 是否通过
     * @param reason 审核原因
     * @return 是否成功
     */
    boolean auditWithdraw(Long withdrawId, Boolean approved, String reason);

    /**
     * 获取财务统计
     *
     * @return 财务统计数据
     */
    FinanceStatistics getFinanceStatistics();

    // ==================== 营销管理（轮播图、公告）====================

    /**
     * 获取轮播图列表
     *
     * @return 轮播图列表
     */
    List<BannerDTO> getBanners();

    /**
     * 创建轮播图
     *
     * @param dto 轮播图信息
     * @return 轮播图ID
     */
    Long createBanner(BannerDTO dto);

    /**
     * 更新轮播图
     *
     * @param bannerId 轮播图ID
     * @param dto 轮播图信息
     */
    void updateBanner(Long bannerId, BannerDTO dto);

    /**
     * 删除轮播图
     *
     * @param bannerId 轮播图ID
     */
    void deleteBanner(Long bannerId);

    /**
     * 更新轮播图状态
     *
     * @param bannerId 轮播图ID
     * @param status 状态
     */
    void updateBannerStatus(Long bannerId, Integer status);

    /**
     * 获取公告列表
     *
     * @param type 公告类型（可选）
     * @return 公告列表
     */
    List<AnnouncementDTO> getAnnouncements(Integer type);

    /**
     * 创建公告
     *
     * @param dto 公告信息
     * @return 公告ID
     */
    Long createAnnouncement(AnnouncementDTO dto);

    /**
     * 更新公告
     *
     * @param announcementId 公告ID
     * @param dto 公告信息
     */
    void updateAnnouncement(Long announcementId, AnnouncementDTO dto);

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     */
    void deleteAnnouncement(Long announcementId);

    /**
     * 更新公告状态
     *
     * @param announcementId 公告ID
     * @param status 状态
     */
    void updateAnnouncementStatus(Long announcementId, Integer status);

    /**
     * 平台统计数据DTO
     */
    class PlatformStatisticsDTO {
        private Long totalUsers;
        private Long todayUsers;
        private Long totalMerchants;
        private Long todayMerchants;
        private Long totalOrders;
        private Long todayOrders;
        private java.math.BigDecimal totalSales;
        private java.math.BigDecimal todaySales;
        private Long totalProducts;
        private Long pendingRefunds;
        private Long pendingAudits;

        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        public Long getTodayUsers() { return todayUsers; }
        public void setTodayUsers(Long todayUsers) { this.todayUsers = todayUsers; }
        public Long getTotalMerchants() { return totalMerchants; }
        public void setTotalMerchants(Long totalMerchants) { this.totalMerchants = totalMerchants; }
        public Long getTodayMerchants() { return todayMerchants; }
        public void setTodayMerchants(Long todayMerchants) { this.todayMerchants = todayMerchants; }
        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public Long getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Long todayOrders) { this.todayOrders = todayOrders; }
        public java.math.BigDecimal getTotalSales() { return totalSales; }
        public void setTotalSales(java.math.BigDecimal totalSales) { this.totalSales = totalSales; }
        public java.math.BigDecimal getTodaySales() { return todaySales; }
        public void setTodaySales(java.math.BigDecimal todaySales) { this.todaySales = todaySales; }
        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
        public Long getPendingRefunds() { return pendingRefunds; }
        public void setPendingRefunds(Long pendingRefunds) { this.pendingRefunds = pendingRefunds; }
        public Long getPendingAudits() { return pendingAudits; }
        public void setPendingAudits(Long pendingAudits) { this.pendingAudits = pendingAudits; }
    }

    /**
     * 财务统计数据DTO
     */
    class FinanceStatistics {
        private java.math.BigDecimal totalBalance;
        private java.math.BigDecimal totalWithdraw;
        private java.math.BigDecimal pendingWithdraw;
        private java.math.BigDecimal completedWithdraw;
        private Long totalWithdrawCount;
        private Long pendingWithdrawCount;

        public java.math.BigDecimal getTotalBalance() { return totalBalance; }
        public void setTotalBalance(java.math.BigDecimal totalBalance) { this.totalBalance = totalBalance; }
        public java.math.BigDecimal getTotalWithdraw() { return totalWithdraw; }
        public void setTotalWithdraw(java.math.BigDecimal totalWithdraw) { this.totalWithdraw = totalWithdraw; }
        public java.math.BigDecimal getPendingWithdraw() { return pendingWithdraw; }
        public void setPendingWithdraw(java.math.BigDecimal pendingWithdraw) { this.pendingWithdraw = pendingWithdraw; }
        public java.math.BigDecimal getCompletedWithdraw() { return completedWithdraw; }
        public void setCompletedWithdraw(java.math.BigDecimal completedWithdraw) { this.completedWithdraw = completedWithdraw; }
        public Long getTotalWithdrawCount() { return totalWithdrawCount; }
        public void setTotalWithdrawCount(Long totalWithdrawCount) { this.totalWithdrawCount = totalWithdrawCount; }
        public Long getPendingWithdrawCount() { return pendingWithdrawCount; }
        public void setPendingWithdrawCount(Long pendingWithdrawCount) { this.pendingWithdrawCount = pendingWithdrawCount; }
    }
}
