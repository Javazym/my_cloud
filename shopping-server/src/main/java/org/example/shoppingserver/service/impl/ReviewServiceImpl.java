package org.example.shoppingserver.service.impl;

import org.example.shoppingserver.model.dto.ReviewDTO;
import org.example.shoppingserver.service.ReviewService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Override
    public Page<ReviewDTO> getProductReviews(Long productId, Integer rating, Boolean hasImage, int pageNum, int pageSize) {
        return null;
    }

    @Override
    @Cacheable(value = "reviewDetail", key = "#reviewId", unless = "#result == null")
    public ReviewDTO getReviewDetail(Long reviewId) {
        return null;
    }

    @Override
    @CacheEvict(value = {"reviewDetail", "reviewStatistics"}, allEntries = true)
    public boolean addReview(String userId, Long orderId, ReviewDTO reviewDTO) {
        return false;
    }

    @Override
    @CacheEvict(value = "reviewDetail", allEntries = true)
    public boolean replyReview(Long merchantId, Long reviewId, String content) {
        return false;
    }

    @Override
    @CacheEvict(value = "reviewDetail", key = "#reviewId")
    public boolean likeReview(Long reviewId) {
        return false;
    }

    @Override
    @CacheEvict(value = {"reviewDetail", "reviewStatistics"}, allEntries = true)
    public boolean deleteReview(String userId, Long reviewId) {
        return false;
    }

    @Override
    @Cacheable(value = "reviewStatistics", key = "#productId", unless = "#result == null")
    public ReviewStatisticsDTO getReviewStatistics(Long productId) {
        return null;
    }
}
