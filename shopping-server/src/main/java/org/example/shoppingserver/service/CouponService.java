package org.example.shoppingserver.service;


import org.example.shoppingserver.model.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券Service接口
 */
public interface CouponService {

    /**
     * 获取优惠券列表
     *
     * @param queryDTO 查询DTO
     * @return 优惠券分页结果
     */
    Page<Coupon> getCoupons(CouponQueryDTO queryDTO);

    /**
     * 获取可用优惠券列表
     *
     * @param userId   用户ID
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    List<Coupon> getAvailableCoupons(String userId, java.math.BigDecimal orderAmount);

    /**
     * 获取优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    Coupon getCouponDetail(Long couponId);

    /**
     * 领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean receiveCoupon(String userId, Long couponId);

    /**
     * 获取用户已领取的优惠券
     *
     * @param userId  用户ID
     * @param status  状态：0-未使用，1-已使用，2-已过期
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户优惠券分页结果
     */
    Page<UserCouponDTO> getUserCoupons(String userId, Integer status, int pageNum, int pageSize);

    /**
     * 验证优惠券
     *
     * @param userId    用户ID
     * @param couponId  优惠券ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    ValidateResultDTO validateCoupon(String userId, Long couponId, java.math.BigDecimal orderAmount);

    /**
     * 使用优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param orderId  订单ID
     * @return 是否成功
     */
    boolean useCoupon(String userId, Long couponId, Long orderId);

    /**
     * 优惠券查询DTO
     */
    class CouponQueryDTO {
        private Long merchantId;
        private Integer status;
        private Integer pageNum = 1;
        private Integer pageSize = 10;

        public Long getMerchantId() { return merchantId; }
        public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public Integer getPageNum() { return pageNum; }
        public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    }

    /**
     * 用户优惠券DTO
     */
    class UserCouponDTO {
        private Long id;
        private Long couponId;
        private String couponName;
        private Integer couponType;
        private java.math.BigDecimal value;
        private java.math.BigDecimal minAmount;
        private java.math.BigDecimal maxDiscount;
        private Integer status;
        private java.time.LocalDateTime expireTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCouponId() { return couponId; }
        public void setCouponId(Long couponId) { this.couponId = couponId; }
        public String getCouponName() { return couponName; }
        public void setCouponName(String couponName) { this.couponName = couponName; }
        public Integer getCouponType() { return couponType; }
        public void setCouponType(Integer couponType) { this.couponType = couponType; }
        public java.math.BigDecimal getValue() { return value; }
        public void setValue(java.math.BigDecimal value) { this.value = value; }
        public java.math.BigDecimal getMinAmount() { return minAmount; }
        public void setMinAmount(java.math.BigDecimal minAmount) { this.minAmount = minAmount; }
        public java.math.BigDecimal getMaxDiscount() { return maxDiscount; }
        public void setMaxDiscount(java.math.BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public java.time.LocalDateTime getExpireTime() { return expireTime; }
        public void setExpireTime(java.time.LocalDateTime expireTime) { this.expireTime = expireTime; }
    }

    /**
     * 验证结果DTO
     */
    class ValidateResultDTO {
        private boolean valid;
        private String message;
        private java.math.BigDecimal discountAmount;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public java.math.BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(java.math.BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    }
}