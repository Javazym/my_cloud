package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

@Data
public class CouponStatistics {
    private Long totalCoupons;
    private Long activeCoupons;
    private Long totalReceived;
    private Long totalUsed;
}
