package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加购物车DTO
 */
@Data
public class AddCartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 数量
     */
    private Integer quantity = 1;
}
