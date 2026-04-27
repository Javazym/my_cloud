package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商家分组展示DTO
 */
@Data
public class MerchantGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称（主营类目）
     */
    private String categoryName;

    /**
     * 分组描述
     */
    private String categoryDescription;

    /**
     * 商家数量
     */
    private Long merchantCount;

    /**
     * 商家列表
     */
    private List<MerchantDTO> merchants;
}
