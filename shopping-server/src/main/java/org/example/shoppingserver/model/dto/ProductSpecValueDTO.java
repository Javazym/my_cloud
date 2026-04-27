package org.example.shoppingserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格值DTO
 */
@Data
public class ProductSpecValueDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    @NotBlank(message = "规格值不能为空")
    private String value;
    private Integer sort = 0;
}
