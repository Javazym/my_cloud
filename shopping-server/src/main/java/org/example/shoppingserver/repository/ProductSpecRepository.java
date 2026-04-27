package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品规格Repository
 */
@Repository
public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {

    /**
     * 根据商品ID查询规格列表
     */
    List<ProductSpec> findByProductId(Long productId);

    /**
     * 根据商品ID查询规格列表（按排序）
     */
    List<ProductSpec> findByProductIdOrderBySortAsc(Long productId);

    /**
     * 统计商品规格数量
     */
    long countByProductId(Long productId);
}
