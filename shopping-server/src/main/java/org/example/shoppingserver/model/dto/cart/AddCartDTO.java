package org.example.shoppingserver.model.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * SKU ID
     */
    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity = 1;
}
