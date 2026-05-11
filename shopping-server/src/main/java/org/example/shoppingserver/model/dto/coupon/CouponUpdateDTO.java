package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class CouponUpdateDTO {
    private String name;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer totalStock;
    private Integer limitPerUser;
    private LocalDateTime endTime;
    private Integer status;
    private Long merchantId;
}
