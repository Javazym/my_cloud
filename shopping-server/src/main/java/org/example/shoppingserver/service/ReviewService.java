package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.review.ReviewDTO;
import org.example.shoppingserver.model.vo.review.ReviewVO;
import org.example.shoppingserver.model.vo.review.ReviewStatisticsVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 评价Service接口
 */

public interface ReviewService {

    /**
     * 获取商品评价列表
     *
     * @param productId 商品ID
     * @param rating   评分筛选
     * @param hasImage 是否仅看有图
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 评价分页结果
     */
    Page<ReviewVO> getProductReviews(Long productId, Integer rating, Boolean hasImage, int pageNum, int pageSize);

    /**
     * 获取评价详情
     *
     * @param reviewId 评价ID
     * @return 评价详情
     */
    ReviewVO getReviewDetail(Long reviewId);

    /**
     * 添加评价
     *
     * @param userId    用户ID
     * @param orderId   订单ID
     * @param reviewDTO 评价DTO
     * @return 是否成功
     */
    boolean addReview(String userId, Long orderId, ReviewDTO reviewDTO);

    /**
     * 商家回复评价
     *
     * @param merchantId 商家ID
     * @param reviewId   评价ID
     * @param content   回复内容
     * @return 是否成功
     */
    boolean replyReview(Long merchantId, Long reviewId, String content);

    /**
     * 点赞评价
     *
     * @param reviewId 评价ID
     * @return 是否成功
     */
    boolean likeReview(Long reviewId);

    /**
     * 删除评价
     *
     * @param userId   用户ID
     * @param reviewId 评价ID
     * @return 是否成功
     */
    boolean deleteReview(String userId, Long reviewId);

    /**
     * 获取评价统计
     *
     * @param productId 商品ID
     * @return 评价统计
     */
    ReviewStatisticsVO getReviewStatistics(Long productId);


}
