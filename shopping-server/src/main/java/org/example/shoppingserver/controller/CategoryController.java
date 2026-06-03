package org.example.shoppingserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.product.CategoryDTO;
import org.example.shoppingserver.model.vo.product.CategoryVO;
import org.example.shoppingserver.service.CategoryService;
import org.example.shoppingserver.util.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    @GetMapping("/tree")
    public ResponseResult<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = categoryService.getCategoryTree();
        return ResponseResult.success(tree);
    }

    /**
     * 获取指定层级的分类列表
     *
     * @param level 层级(1-一级, 2-二级, 3-三级)
     * @return 分类列表
     */
    @GetMapping("/level/{level}")
    public ResponseResult<List<CategoryVO>> getCategoriesByLevel(@PathVariable Integer level) {
        List<CategoryVO> categories = categoryService.getCategoriesByLevel(level);
        return ResponseResult.success(categories);
    }

    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    @GetMapping("/{categoryId}")
    public ResponseResult<CategoryVO> getCategoryById(@PathVariable Long categoryId) {
        CategoryVO category = categoryService.getCategoryById(categoryId);
        return ResponseResult.success(category);
    }

    /**
     * 创建分类
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    @RequireRole(value = {"ROLE_ADMIN"})
    @PostMapping
    public ResponseResult<Long> createCategory(@Valid @RequestBody CategoryDTO dto) {
        Long categoryId = categoryService.createCategory(dto);
        return ResponseResult.success(categoryId);
    }

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param dto 分类信息
     * @return 操作结果
     */
    @RequireRole(value = {"ROLE_ADMIN"})
    @PutMapping("/{categoryId}")
    public ResponseResult<?> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(categoryId, dto);
        return ResponseResult.success();
    }

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 操作结果
     */
    @RequireRole(value = {"ROLE_ADMIN"})
    @DeleteMapping("/{categoryId}")
    public ResponseResult<?> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseResult.success();
    }

    /**
     * 更新分类状态
     *
     * @param categoryId 分类ID
     * @param status 状态(0-禁用, 1-启用)
     * @return 操作结果
     */
    @RequireRole(value = {"ROLE_ADMIN"})
    @PutMapping("/{categoryId}/status")
    public ResponseResult<?> updateCategoryStatus(
            @PathVariable Long categoryId,
            @RequestParam Integer status) {
        categoryService.updateCategoryStatus(categoryId, status);
        return ResponseResult.success("删除成功");
    }

    /**
     * 更新分类排序
     *
     * @param categoryId 分类ID
     * @param sort 排序值
     * @return 操作结果
     */
    @RequireRole(value = {"ROLE_ADMIN"})
    @PutMapping("/{categoryId}/sort")
    public ResponseResult<?> updateCategorySort(
            @PathVariable Long categoryId,
            @RequestParam Integer sort) {
        categoryService.updateCategorySort(categoryId, sort);
        return ResponseResult.success();
    }
}
