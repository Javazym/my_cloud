package org.example.shoppingserver.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品创建DTO
 */
@Data
public class ProductCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称不能超过200个字符")
    private String name;

    @Size(max = 200, message = "副标题不能超过200个字符")
    private String subName;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    private String image;
    private List<String> images;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "原价必须大于0")
    @Digits(integer = 8, fraction = 2, message = "原价格式不正确")
    private BigDecimal originalPrice;

    private BigDecimal costPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;
    private String description;
    private String detail;
    private Integer isHot = 0;
    private Integer isFeatured = 0;
    private Integer isNew = 0;
    private Integer publishStatus = 1;

    @Size(max = 500, message = "标签不能超过500个字符")
    private String tags;

    @Size(max = 255, message = "关键词不能超过255个字符")
    private String keywords;

    @Valid
    private List<ProductSpecDTO> specs;

    @Valid
    private List<ProductSkuCreateDTO> skus;
}
