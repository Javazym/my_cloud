package org.example.shoppingserver.model.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品SKU创建DTO
 */
@Data
public class ProductSkuCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "SKU编码不能为空")
    @Size(max = 64, message = "SKU编码不能超过64个字符")
    private String skuCode;

    @NotNull(message = "规格组合不能为空")
    private Map<String, String> specs;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "原价必须大于0")
    @Digits(integer = 8, fraction = 2, message = "原价格式不正确")
    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    @Min(value = 0, message = "库存预警值不能小于0")
    private Integer lowStock = 10;
    private String image;
    private Integer status = 1;
}
