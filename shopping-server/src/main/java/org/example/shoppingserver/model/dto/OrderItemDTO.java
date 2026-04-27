package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品DTO
 */
@Data
public class OrderItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单商品ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU规格
     */
    private String skuSpecs;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 评价状态
     */
    private Integer reviewStatus;
}
