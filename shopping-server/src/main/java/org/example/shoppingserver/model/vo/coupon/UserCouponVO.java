package org.example.shoppingserver.model.vo.coupon;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 */
@Data
public class UserCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private BigDecimal value;

    /**
     * 最低消费
     */
    private BigDecimal minAmount;

    /**
     * 最高优惠
     */
    private BigDecimal maxDiscount;

    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
