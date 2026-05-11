package org.example.shoppingserver.model.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项VO
 */
@Data
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
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
     * 数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    private BigDecimal totalPrice;

    /**
     * 评价状态（0-未评价，1-已评价）
     */
    private Integer reviewStatus;
}
