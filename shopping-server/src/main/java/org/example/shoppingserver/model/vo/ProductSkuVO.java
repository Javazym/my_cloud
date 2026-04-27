package org.example.shoppingserver.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商品SKU VO
 */
@Data
public class ProductSkuVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String skuCode;
    private Map<String, String> specs;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer lowStock;
    private String image;
    private Integer status;
    private Boolean lowStockStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
