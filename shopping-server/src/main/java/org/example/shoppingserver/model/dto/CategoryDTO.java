package org.example.shoppingserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类DTO
 */
@Data
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID(更新时必填)
     */
    private Long id;

    /**
     * 父分类ID(NULL表示顶级分类)
     */
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
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
     * 排序值(数值越小越靠前)
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 子分类列表
     */
    private List<CategoryDTO> children;
}
