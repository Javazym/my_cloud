package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.common.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 查询顶级分类
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 根据层级查询分类
     */
    List<Category> findByLevel(Integer level);

    /**
     * 查询顶级分类（按排序）
     */
    @Query("SELECT c FROM Category c WHERE c.parentId IS NULL ORDER BY c.sort ASC")
    List<Category> findTopCategories();

    /**
     * 查询子分类
     */
    @Query("SELECT c FROM Category c WHERE c.parentId = :parentId ORDER BY c.sort ASC")
    List<Category> findChildren(@Param("parentId") Long parentId);

    /**
     * 根据名称查询分类
     */
    Category findByName(String name);


    /**
     * 统计子分类数量
     */
    long countByParentId(Long parentId);
}
