package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.MerchantApplicationDTO;
import org.example.shoppingserver.model.dto.MerchantDTO;

import java.util.List;

/**
 * 商家Service接口
 */

public interface MerchantService {

    /**
     * 申请入驻
     *
     * @param userId    用户ID
     * @param applyDTO  申请DTO
     * @return 申请结果
     */
    boolean applySettle(String userId, ApplySettleDTO applyDTO);

    /**
     * 获取商家信息
     *
     * @param userId 用户ID
     * @return 商家信息
     */
    MerchantDTO getMerchantInfo(String userId);

    /**
     * 获取商家信息（根据用户ID）
     *
     * @param userId 用户ID
     * @return 商家信息
     */
    MerchantDTO getMerchantByUserId(String userId);

    /**
     * 更新商家信息
     *
     * @param userId 用户ID
     * @param merchantDTO 商家DTO
     * @return 商家信息
     */
    MerchantDTO updateMerchantInfo(String userId, MerchantDTO merchantDTO);

    /**
     * 获取商家统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    MerchantStatisticsDTO getStatistics(String userId);

    MerchantApplicationDTO getMerchantApplication(String userId);

    /**
     * 商家入驻申请DTO
     */
    class ApplySettleDTO {
        private String storeName;
        private String storeType;
        private List<String> category;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        private String businessLicense;
        private String idCardFront;
        private String idCardBack;

        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        public String getStoreType() { return storeType; }
        public void setStoreType(String storeType) { this.storeType = storeType; }
        public List<String> getCategory() { return category; }
        public void setCategory(List<String> category) { this.category = category; }
        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getBusinessLicense() { return businessLicense; }
        public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
        public String getIdCardFront() { return idCardFront; }
        public void setIdCardFront(String idCardFront) { this.idCardFront = idCardFront; }
        public String getIdCardBack() { return idCardBack; }
        public void setIdCardBack(String idCardBack) { this.idCardBack = idCardBack; }
    }

    /**
     * 商家统计数据DTO
     */
    class MerchantStatisticsDTO {
        private Long todaySales;
        private Integer todayOrders;
        private Integer todayVisitors;
        private Integer pendingOrders;
        private Integer shippingOrders;
        private Integer totalProducts;
        private Long totalIncome;

        public Long getTodaySales() { return todaySales; }
        public void setTodaySales(Long todaySales) { this.todaySales = todaySales; }
        public Integer getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }
        public Integer getTodayVisitors() { return todayVisitors; }
        public void setTodayVisitors(Integer todayVisitors) { this.todayVisitors = todayVisitors; }
        public Integer getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Integer pendingOrders) { this.pendingOrders = pendingOrders; }
        public Integer getShippingOrders() { return shippingOrders; }
        public void setShippingOrders(Integer shippingOrders) { this.shippingOrders = shippingOrders; }
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        public Long getTotalIncome() { return totalIncome; }
        public void setTotalIncome(Long totalIncome) { this.totalIncome = totalIncome; }
    }
}
