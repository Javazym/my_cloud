package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * 根据商家ID查询商品列表
     */
    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据分类ID查询商品列表
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);



    /**
     * 查询热卖商品
     */
    @Query("SELECT p FROM Product p WHERE p.isHot = 1 AND p.publishStatus = 1 ORDER BY p.soldCount DESC")
    List<Product> findHotProducts(Pageable pageable);

    /**
     * 查询精选商品
     */
    @Query("SELECT p FROM Product p WHERE p.isFeatured = 1 AND p.publishStatus = 1 ORDER BY p.rating DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    /**
     * 查询新品
     */
    @Query("SELECT p FROM Product p WHERE p.isNew = 1 AND p.publishStatus = 1 ORDER BY p.createdAt DESC")
    List<Product> findNewProducts(Pageable pageable);

    /**
     * 根据关键字搜索商品
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.keywords LIKE %:keyword% AND p.publishStatus = 1")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询上架商品
     */
    Page<Product> findByPublishStatus(Integer publishStatus, Pageable pageable);

    /**
     * 批量更新上架状态
     */
    @Modifying
    @Query("UPDATE Product p SET p.publishStatus = :status WHERE p.id IN :ids")
    void batchUpdatePublishStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 统计商家商品数量
     */
    long countByMerchantId(Long merchantId);

    /**
     * 统计分类商品数量
     */
    long countByCategoryId(Long categoryId);

    /**
     * 查询推荐商品（热卖或精选）
     */
    @Query("SELECT p FROM Product p WHERE (p.isHot = 1 OR p.isFeatured = 1) AND p.publishStatus = 1 ORDER BY p.soldCount DESC")
    List<Product> findRecommendedProducts(Pageable pageable);


    /**
     * 根据审核状态查询商品列表
     */
    Page<Product> findAllByAuditStatus(AuditStatus auditStatus, Pageable pageable);

    List<Product> findByAuditStatus(AuditStatus auditStatus, Pageable pageable);
}
