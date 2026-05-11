package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.product.CategoryDTO;
import org.example.shoppingserver.model.vo.product.CategoryVO;

import java.util.List;

/**
 * 商品分类Service接口
 */
public interface CategoryService {

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取指定层级的分类列表
     *
     * @param level 层级
     * @return 分类列表
     */
    List<CategoryVO> getCategoriesByLevel(Integer level);

    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    CategoryVO getCategoryById(Long categoryId);

    /**
     * 创建分类
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    Long createCategory(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param dto 分类信息
     */
    void updateCategory(Long categoryId, CategoryDTO dto);

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);

    /**
     * 更新分类状态
     *
     * @param categoryId 分类ID
     * @param status 状态
     */
    void updateCategoryStatus(Long categoryId, Integer status);

    /**
     * 更新分类排序
     *
     * @param categoryId 分类ID
     * @param sort 排序值
     */
    void updateCategorySort(Long categoryId, Integer sort);
}
