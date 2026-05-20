package org.example.shoppingserver.model.entity.coupon;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_coupons")
public class UserCoupon extends BaseEntity {

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 优惠券ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    /**
     * 优惠券名称
     */
    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;

    /**
     * 优惠券类型
     */
    @Column(name = "coupon_type")
    private Integer couponType = 0;

    /**
     * 面值/折扣
     */
    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    /**
     * 最低消费
     */
    @Column(name = "min_amount", precision = 10, scale = 2)
    private BigDecimal minAmount = BigDecimal.ZERO;

    /**
     * 最高优惠
     */
    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    /**
     * 使用的订单ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    @Column(name = "status")
    private Integer status = 0;

    /**
     * 领取时间
     */
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    /**
     * 使用时间
     */
    @Column(name = "use_time")
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否可用
     */
    public boolean isAvailable() {
        return status == 0 && (expireTime == null || LocalDateTime.now().isBefore(expireTime));
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 计算优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (couponType == 0) {
            // 满减券
            discount = value;
        } else {
            // 折扣券
            discount = orderAmount.multiply(BigDecimal.ONE.subtract(value.divide(new BigDecimal("100"))));
        }

        // 最高优惠限制
        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }

        return discount;
    }
}
