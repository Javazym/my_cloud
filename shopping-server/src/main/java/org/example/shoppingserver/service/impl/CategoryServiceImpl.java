package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.dto.CategoryDTO;
import org.example.shoppingserver.model.entity.Category;
import org.example.shoppingserver.model.vo.CategoryVO;
import org.example.shoppingserver.repository.CategoryRepository;
import org.example.shoppingserver.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类Service实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有顶级分类
        List<Category> topCategories = categoryRepository.findTopCategories();
        return topCategories.stream()
                .map(category -> buildCategoryTree(category))
                .collect(Collectors.toList());
    }

    /**
     * 递归构建分类树
     */
    private CategoryVO buildCategoryTree(Category category) {
        CategoryVO vo = convertToVO(category);
        
        // 查询子分类
        List<Category> children = categoryRepository.findChildren(category.getId());
        if (children != null && !children.isEmpty()) {
            List<CategoryVO> childrenVO = children.stream()
                    .map(this::buildCategoryTree)
                    .collect(Collectors.toList());
            vo.setChildren(childrenVO);
        } else {
            vo.setChildren(new ArrayList<>());
        }
        
        return vo;
    }

    @Override
    public List<CategoryVO> getCategoriesByLevel(Integer level) {
        List<Category> categories = categoryRepository.findByLevel(level);
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        return convertToVO(category);
    }

    @Override
    @Transactional
    public Long createCategory(CategoryDTO dto) {
        // 检查分类名称是否已存在
        Category existingCategory = categoryRepository.findByName(dto.getName());
        if (existingCategory != null) {
            throw new RuntimeException("分类名称已存在");
        }

        // 如果是子分类，验证父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() != 0) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在"));
            
            // 设置层级
            dto.setLevel(parent.getLevel() + 1);
            
            // 最多支持3级分类
            if (dto.getLevel() > 3) {
                throw new RuntimeException("最多支持3级分类");
            }
        } else {
            // 顶级分类
            dto.setParentId(null);
            dto.setLevel(1);
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        
        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 如果要修改父分类，需要验证
        if (dto.getParentId() != null && !dto.getParentId().equals(category.getParentId())) {
            // 不能将自己设置为自己的父分类
            if (dto.getParentId().equals(categoryId)) {
                throw new RuntimeException("不能将自己设置为父分类");
            }

            // 验证新的父分类是否存在
            if (dto.getParentId() != null && dto.getParentId() != 0) {
                Category parent = categoryRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new RuntimeException("父分类不存在"));
                
                // 检查是否会形成循环引用
                if (isDescendant(categoryId, dto.getParentId())) {
                    throw new RuntimeException("不能将子分类设置为父分类");
                }
                
                // 更新层级
                dto.setLevel(parent.getLevel() + 1);
            } else {
                dto.setLevel(1);
            }
        }

        // 更新字段
        if (dto.getName() != null) {
            // 检查名称是否与其他分类重复
            Category existingCategory = categoryRepository.findByName(dto.getName());
            if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
                throw new RuntimeException("分类名称已存在");
            }
            category.setName(dto.getName());
        }
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
        if (dto.getImage() != null) {
            category.setImage(dto.getImage());
        }
        if (dto.getSort() != null) {
            category.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            category.setStatus(dto.getStatus());
        }
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }
        if (dto.getLevel() != null) {
            category.setLevel(dto.getLevel());
        }

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 检查是否有子分类
        long childCount = categoryRepository.countByParentId(categoryId);
        if (childCount > 0) {
            throw new RuntimeException("该分类下还有子分类，无法删除");
        }

        // 检查是否有商品使用该分类
        // TODO: 添加商品关联检查
        
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long categoryId, Integer status) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        category.setStatus(status);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategorySort(Long categoryId, Integer sort) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        category.setSort(sort);
        categoryRepository.save(category);
    }

    /**
     * 检查targetId是否是categoryId的后代节点
     */
    private boolean isDescendant(Long categoryId, Long targetId) {
        Category target = categoryRepository.findById(targetId).orElse(null);
        if (target == null) {
            return false;
        }

        // 向上遍历，检查是否会遇到categoryId
        Long currentParentId = target.getParentId();
        while (currentParentId != null) {
            if (currentParentId.equals(categoryId)) {
                return true;
            }
            Category parent = categoryRepository.findById(currentParentId).orElse(null);
            if (parent == null) {
                break;
            }
            currentParentId = parent.getParentId();
        }
        return false;
    }

    /**
     * 转换Entity为VO
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
