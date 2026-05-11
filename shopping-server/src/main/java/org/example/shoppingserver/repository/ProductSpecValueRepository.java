package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.product.ProductSpecValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品规格值Repository
 */
@Repository
public interface ProductSpecValueRepository extends JpaRepository<ProductSpecValue, Long> {

    /**
     * 根据规格ID查询规格值列表
     */
    List<ProductSpecValue> findBySpecId(Long specId);

    /**
     * 根据规格ID查询规格值列表（按排序）
     */
    List<ProductSpecValue> findBySpecIdOrderBySortAsc(Long specId);

    /**
     * 统计规格值数量
     */
    long countBySpecId(Long specId);
}
