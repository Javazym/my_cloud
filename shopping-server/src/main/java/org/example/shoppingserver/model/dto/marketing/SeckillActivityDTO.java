package org.example.shoppingserver.model.dto.marketing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillActivityDTO {
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
    private Long merchantId;
}
