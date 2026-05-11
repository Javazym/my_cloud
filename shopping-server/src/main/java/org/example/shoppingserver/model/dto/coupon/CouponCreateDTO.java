package org.example.shoppingserver.model.dto.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponCreateDTO {
    private String name;
    private Integer type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer totalStock;
    private Integer limitPerUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime endTime;
}
