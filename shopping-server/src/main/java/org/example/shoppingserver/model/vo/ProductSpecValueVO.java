package org.example.shoppingserver.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格值VO
 */
@Data
public class ProductSpecValueVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String value;
    private Integer sort;
}
