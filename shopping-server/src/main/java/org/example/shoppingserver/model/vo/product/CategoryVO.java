package org.example.shoppingserver.model.vo.product;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类VO
 */
@Data
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级：1-一级，2-二级，3-三级
     */
    private Integer level;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 分类图片
     */
    private String image;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 子分类列表
     */
    private List<CategoryVO> children;
}
