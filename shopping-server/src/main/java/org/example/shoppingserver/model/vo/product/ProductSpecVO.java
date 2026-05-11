package org.example.shoppingserver.model.vo.product;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格VO
 */
@Data
public class ProductSpecVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Integer sort;
    private List<ProductSpecValueVO> values;
}
