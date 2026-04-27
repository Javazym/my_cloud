package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
}
