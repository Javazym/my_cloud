package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.product.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品SKU Repository
 */
@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    /**
     * 根据商品ID查询SKU列表
     */
    List<ProductSku> findByProductId(Long productId);

    /**
     * 根据SKU编码查询SKU
     */
    Optional<ProductSku> findBySkuCode(String skuCode);

    /**
     * 根据商品ID查询启用的SKU列表
     */
    List<ProductSku> findByProductIdAndStatus(Long productId, Integer status);

    /**
     * 扣减库存
     */
    @Modifying
    @Query("UPDATE ProductSku p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加库存
     */
    @Modifying
    @Query("UPDATE ProductSku p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    int addStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 查询库存不足的SKU
     */
    @Query("SELECT p FROM ProductSku p WHERE p.stock <= p.lowStock AND p.status = 1")
    List<ProductSku> findLowStockSkus();

    /**
     * 统计商品SKU数量
     */
    long countByProductId(Long productId);
}
