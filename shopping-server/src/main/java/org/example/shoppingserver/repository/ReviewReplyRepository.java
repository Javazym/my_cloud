package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.review.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评价回复Repository
 */
@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    /**
     * 根据评价ID查询回复
     */
    List<ReviewReply> findByReviewId(Long reviewId);

    /**
     * 根据评价ID查询回复（单个）
     */
    Optional<ReviewReply> findByReviewIdAndMerchantId(Long reviewId, Long merchantId);

    /**
     * 统计评价回复数量
     */
    long countByReviewId(Long reviewId);
}
