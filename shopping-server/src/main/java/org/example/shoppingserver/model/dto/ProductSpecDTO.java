package org.example.shoppingserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格DTO
 */
@Data
public class ProductSpecDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "规格名称不能为空")
    private String name;
    private Integer sort = 0;

    @NotNull(message = "规格值列表不能为空")
    private List<ProductSpecValueDTO> values;
}
