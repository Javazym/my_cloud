package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.ReviewDTO;
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
    Page<ReviewDTO> getProductReviews(Long productId, Integer rating, Boolean hasImage, int pageNum, int pageSize);

    /**
     * 获取评价详情
     *
     * @param reviewId 评价ID
     * @return 评价详情
     */
    ReviewDTO getReviewDetail(Long reviewId);

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
    ReviewStatisticsDTO getReviewStatistics(Long productId);

    /**
     * 评价统计DTO
     */
    class ReviewStatisticsDTO {
        private Long totalCount;
        private Double averageRating;
        private Integer fiveStarCount;
        private Integer fourStarCount;
        private Integer threeStarCount;
        private Integer twoStarCount;
        private Integer oneStarCount;
        private Double positiveRate;
        private Integer withImageCount;

        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        public Integer getFiveStarCount() { return fiveStarCount; }
        public void setFiveStarCount(Integer fiveStarCount) { this.fiveStarCount = fiveStarCount; }
        public Integer getFourStarCount() { return fourStarCount; }
        public void setFourStarCount(Integer fourStarCount) { this.fourStarCount = fourStarCount; }
        public Integer getThreeStarCount() { return threeStarCount; }
        public void setThreeStarCount(Integer threeStarCount) { this.threeStarCount = threeStarCount; }
        public Integer getTwoStarCount() { return twoStarCount; }
        public void setTwoStarCount(Integer twoStarCount) { this.twoStarCount = twoStarCount; }
        public Integer getOneStarCount() { return oneStarCount; }
        public void setOneStarCount(Integer oneStarCount) { this.oneStarCount = oneStarCount; }
        public Double getPositiveRate() { return positiveRate; }
        public void setPositiveRate(Double positiveRate) { this.positiveRate = positiveRate; }
        public Integer getWithImageCount() { return withImageCount; }
        public void setWithImageCount(Integer withImageCount) { this.withImageCount = withImageCount; }
    }
}
