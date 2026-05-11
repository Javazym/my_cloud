package org.example.shoppingserver.model.entity.common;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    /**
     * 父分类ID：NULL-顶级分类
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 分类名称
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 层级：1-一级，2-二级，3-三级
     */
    @Column(name = "level")
    private Integer level = 1;

    /**
     * 分类图标
     */
    @Column(name = "icon", length = 255)
    private String icon;

    /**
     * 分类图片
     */
    @Column(name = "image", length = 255)
    private String image;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;
}
