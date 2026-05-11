package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户优惠券DTO
 */
@Data
public class UserCouponDTO {
    /**
     * 用户优惠券ID
     */
    private Long id;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠券名称
     */
    private String couponName;
    
    /**
     * 优惠券类型：0-满减，1-折扣
     */
    private Integer couponType;
    
    /**
     * 面值/折扣
     */
    private java.math.BigDecimal value;
    
    /**
     * 最低消费
     */
    private java.math.BigDecimal minAmount;
    
    /**
     * 最高优惠
     */
    private java.math.BigDecimal maxDiscount;
    
    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
