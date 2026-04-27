package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品评价Repository
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * 根据商品ID查询评价列表
     */
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);

    /**
     * 根据商品ID和状态查询评价列表
     */
    Page<ProductReview> findByProductIdAndStatus(Long productId, Integer status, Pageable pageable);

    /**
     * 根据订单ID查询评价列表
     */
    List<ProductReview> findByOrderId(Long orderId);

    /**
     * 根据用户ID查询评价列表
     */
    Page<ProductReview> findByUserId(String userId, Pageable pageable);

    /**
     * 根据评分查询评价
     */
    Page<ProductReview> findByProductIdAndRating(Long productId, Integer rating, Pageable pageable);

    /**
     * 统计商品评价数量
     */
    long countByProductId(Long productId);

    /**
     * 统计商品各评分评价数量
     */
    long countByProductIdAndRating(Long productId, Integer rating);

    /**
     * 计算商品平均评分
     */
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId")
    Double calculateAverageRating(@Param("productId") Long productId);

    /**
     * 统计用户评价数量
     */
    long countByUserId(String userId);

    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE ProductReview r SET r.likeCount = r.likeCount + 1 WHERE r.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 查询有图片的评价
     */
    @Query("SELECT r FROM ProductReview r WHERE r.product.id = :productId AND r.images IS NOT NULL AND r.images != ''")
    Page<ProductReview> findReviewsWithImages(@Param("productId") Long productId, Pageable pageable);
}
