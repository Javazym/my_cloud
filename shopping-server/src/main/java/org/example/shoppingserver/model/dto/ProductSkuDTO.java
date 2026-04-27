package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品SKU DTO
 */
@Data
public class ProductSkuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 规格组合
     */
    private String specs;

    /**
     * 销售价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 库存预警值
     */
    private Integer lowStock;

    /**
     * SKU图片
     */
    private String image;

    /**
     * 状态
     */
    private Integer status;
}
