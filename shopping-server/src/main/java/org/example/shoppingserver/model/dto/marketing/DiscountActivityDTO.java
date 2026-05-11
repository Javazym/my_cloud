package org.example.shoppingserver.model.dto.marketing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountActivityDTO {
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
    private Long activityId;
    private Long merchantId;
}
