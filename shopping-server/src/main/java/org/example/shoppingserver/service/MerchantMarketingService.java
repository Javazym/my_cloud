package org.example.shoppingserver.service;

import org.example.shoppingserver.model.entity.DiscountActivity;
import org.example.shoppingserver.model.entity.SeckillActivity;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家营销服务接口
 */
public interface MerchantMarketingService {

    // ==================== 秒杀活动管理 ====================

    /**
     * 创建秒杀活动
     */
    Long createSeckillActivity(Long merchantId, SeckillActivityDTO dto);

    /**
     * 更新秒杀活动
     */
    void updateSeckillActivity(Long merchantId, Long activityId, SeckillActivityDTO dto);

    /**
     * 删除秒杀活动
     */
    void deleteSeckillActivity(Long merchantId, Long activityId);

    /**
     * 获取秒杀活动列表
     */
    Page<SeckillActivity> getSeckillActivities(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取秒杀活动详情
     */
    SeckillActivity getSeckillActivityDetail(Long merchantId, Long activityId);

    /**
     * 更新秒杀活动状态
     */
    void updateSeckillStatus(Long merchantId, Long activityId, Integer status);

    /**
     * 抢购秒杀商品
     */
    boolean seckillProduct(String userId, Long activityId);

    // ==================== 满减活动管理 ====================

    /**
     * 创建满减活动
     */
    Long createDiscountActivity(Long merchantId, DiscountActivityDTO dto);

    /**
     * 更新满减活动
     */
    void updateDiscountActivity(Long merchantId, Long activityId, DiscountActivityDTO dto);

    /**
     * 删除满减活动
     */
    void deleteDiscountActivity(Long merchantId, Long activityId);

    /**
     * 获取满减活动列表
     */
    Page<DiscountActivity> getDiscountActivities(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取满减活动详情
     */
    DiscountActivity getDiscountActivityDetail(Long merchantId, Long activityId);

    /**
     * 更新满减活动状态
     */
    void updateDiscountStatus(Long merchantId, Long activityId, Integer status);

    /**
     * 计算满减优惠
     */
    BigDecimal calculateDiscount(Long merchantId, BigDecimal orderAmount, Integer quantity);

    // ==================== 优惠券管理（商家端）====================

    /**
     * 创建优惠券
     */
    Long createCoupon(Long merchantId, CouponCreateDTO dto);

    /**
     * 更新优惠券
     */
    void updateCoupon(Long merchantId, Long couponId, CouponUpdateDTO dto);

    /**
     * 删除优惠券
     */
    void deleteCoupon(Long merchantId, Long couponId);

    /**
     * 获取商家优惠券列表
     */
    Page<org.example.shoppingserver.model.entity.Coupon> getMerchantCoupons(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取优惠券统计
     */
    CouponStatistics getCouponStatistics(Long merchantId);

    // ==================== DTO 定义 ====================

    /**
     * 秒杀活动DTO
     */
    class SeckillActivityDTO {
        private String name;
        private Long productId;
        private Long skuId;
        private BigDecimal seckillPrice;
        private BigDecimal originalPrice;
        private Integer stock;
        private Integer limitPerUser;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer sort;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getSkuId() { return skuId; }
        public void setSkuId(Long skuId) { this.skuId = skuId; }
        public BigDecimal getSeckillPrice() { return seckillPrice; }
        public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public Integer getLimitPerUser() { return limitPerUser; }
        public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Integer getSort() { return sort; }
        public void setSort(Integer sort) { this.sort = sort; }
    }

    /**
     * 满减活动DTO
     */
    class DiscountActivityDTO {
        private String name;
        private String description;
        private Integer discountType;
        private BigDecimal conditionValue;
        private BigDecimal discountAmount;
        private BigDecimal maxDiscount;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String scopeType;
        private String scopeIds;
        private Integer limitPerUser;
        private Integer sort;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getDiscountType() { return discountType; }
        public void setDiscountType(Integer discountType) { this.discountType = discountType; }
        public BigDecimal getConditionValue() { return conditionValue; }
        public void setConditionValue(BigDecimal conditionValue) { this.conditionValue = conditionValue; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getMaxDiscount() { return maxDiscount; }
        public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public String getScopeType() { return scopeType; }
        public void setScopeType(String scopeType) { this.scopeType = scopeType; }
        public String getScopeIds() { return scopeIds; }
        public void setScopeIds(String scopeIds) { this.scopeIds = scopeIds; }
        public Integer getLimitPerUser() { return limitPerUser; }
        public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
        public Integer getSort() { return sort; }
        public void setSort(Integer sort) { this.sort = sort; }
    }

    /**
     * 优惠券创建DTO
     */
    class CouponCreateDTO {
        private String name;
        private Integer type;
        private BigDecimal value;
        private BigDecimal minAmount;
        private BigDecimal maxDiscount;
        private Integer totalStock;
        private Integer limitPerUser;
        private Integer validDays;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        public BigDecimal getMinAmount() { return minAmount; }
        public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
        public BigDecimal getMaxDiscount() { return maxDiscount; }
        public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
        public Integer getTotalStock() { return totalStock; }
        public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
        public Integer getLimitPerUser() { return limitPerUser; }
        public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
        public Integer getValidDays() { return validDays; }
        public void setValidDays(Integer validDays) { this.validDays = validDays; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }

    /**
     * 优惠券更新DTO
     */
    class CouponUpdateDTO {
        private String name;
        private BigDecimal value;
        private BigDecimal minAmount;
        private BigDecimal maxDiscount;
        private Integer totalStock;
        private Integer limitPerUser;
        private LocalDateTime endTime;
        private Integer status;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        public BigDecimal getMinAmount() { return minAmount; }
        public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
        public BigDecimal getMaxDiscount() { return maxDiscount; }
        public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
        public Integer getTotalStock() { return totalStock; }
        public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
        public Integer getLimitPerUser() { return limitPerUser; }
        public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }

    /**
     * 优惠券统计
     */
    class CouponStatistics {
        private Long totalCoupons;
        private Long activeCoupons;
        private Long totalReceived;
        private Long totalUsed;

        public Long getTotalCoupons() { return totalCoupons; }
        public void setTotalCoupons(Long totalCoupons) { this.totalCoupons = totalCoupons; }
        public Long getActiveCoupons() { return activeCoupons; }
        public void setActiveCoupons(Long activeCoupons) { this.activeCoupons = activeCoupons; }
        public Long getTotalReceived() { return totalReceived; }
        public void setTotalReceived(Long totalReceived) { this.totalReceived = totalReceived; }
        public Long getTotalUsed() { return totalUsed; }
        public void setTotalUsed(Long totalUsed) { this.totalUsed = totalUsed; }
    }
}
