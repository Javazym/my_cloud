package org.example.shoppingserver.model.vo.merchant;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商家分组VO
 */
@Data
public class MerchantGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类目名称
     */
    private String categoryName;

    /**
     * 类目描述
     */
    private String categoryDescription;

    /**
     * 商家数量
     */
    private Long merchantCount;

    /**
     * 商家列表
     */
    private List<MerchantVO> merchants;
}
