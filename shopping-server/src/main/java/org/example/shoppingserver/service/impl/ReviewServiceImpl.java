package org.example.shoppingserver.service.impl;

import org.example.shoppingserver.model.dto.ReviewDTO;
import org.example.shoppingserver.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Override
    public Page<ReviewDTO> getProductReviews(Long productId, Integer rating, Boolean hasImage, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public ReviewDTO getReviewDetail(Long reviewId) {
        return null;
    }

    @Override
    public boolean addReview(String userId, Long orderId, ReviewDTO reviewDTO) {
        return false;
    }

    @Override
    public boolean replyReview(Long merchantId, Long reviewId, String content) {
        return false;
    }

    @Override
    public boolean likeReview(Long reviewId) {
        return false;
    }

    @Override
    public boolean deleteReview(String userId, Long reviewId) {
        return false;
    }

    @Override
    public ReviewStatisticsDTO getReviewStatistics(Long productId) {
        return null;
    }
}
