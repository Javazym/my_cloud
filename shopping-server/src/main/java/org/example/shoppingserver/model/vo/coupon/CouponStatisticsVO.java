package org.example.shoppingserver.model.vo.coupon;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券统计VO
 */
@Data
public class CouponStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总优惠券数
     */
    private Long totalCoupons;

    /**
     * 进行中的优惠券数
     */
    private Long activeCoupons;

    /**
     * 总领取数
     */
    private Long totalReceived;

    /**
     * 总使用数
     */
    private Long totalUsed;
}
