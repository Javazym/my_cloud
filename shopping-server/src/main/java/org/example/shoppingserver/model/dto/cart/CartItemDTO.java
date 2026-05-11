package org.example.shoppingserver.model.dto.cart;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车商品DTO
 */
@Data
public class CartItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

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
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计
     */
    private BigDecimal subtotal;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 是否选中
     */
    private Integer checked;

    /**
     * 商品是否有效
     */
    private Boolean valid;
}
