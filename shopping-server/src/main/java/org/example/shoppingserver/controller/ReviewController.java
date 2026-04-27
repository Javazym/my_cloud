package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.ReviewDTO;
import org.example.shoppingserver.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

 @Autowired
 private final ReviewService reviewService;

 /**
 * 获取商品评价列表
 */
 @GetMapping("/product/{productId}")
 public ResponseResult<Page<ReviewDTO>> getProductReviews( @PathVariable Long productId, @RequestParam(required = false) Integer rating, @RequestParam(required = false) Boolean hasImage, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<ReviewDTO> page = reviewService.getProductReviews(productId, rating, hasImage, pageNum, pageSize);
 return ResponseResult.success(page);
 }

 /**
 * 获取评价详情
 */
 @GetMapping("/{reviewId}")
 public ResponseResult<ReviewDTO> getReviewDetail( @PathVariable Long reviewId) {
 ReviewDTO review = reviewService.getReviewDetail(reviewId);
 return ResponseResult.success(review);
 }

 /**
 * 添加评价
 */
 @PostMapping
 public ResponseResult<Boolean> addReview( @RequestParam String userId, @RequestParam Long orderId,
 @RequestBody ReviewDTO reviewDTO) {
 boolean result = reviewService.addReview(userId, orderId, reviewDTO);
 return ResponseResult.success(result);
 }

 /**
 * 商家回复评价
 */
 @PostMapping("/{reviewId}/reply")
 public ResponseResult<Boolean> replyReview( @RequestParam Long merchantId, @PathVariable Long reviewId, @RequestParam String content) {
 boolean result = reviewService.replyReview(merchantId, reviewId, content);
 return ResponseResult.success(result);
 }

 /**
 * 点赞评价
 */
 @PostMapping("/{reviewId}/like")
 public ResponseResult<Boolean> likeReview( @PathVariable Long reviewId) {
 boolean result = reviewService.likeReview(reviewId);
 return ResponseResult.success(result);
 }

 /**
 * 删除评价
 */
 @DeleteMapping("/{reviewId}")
 public ResponseResult<Boolean> deleteReview( @RequestParam String userId, @PathVariable Long reviewId) {
 boolean result = reviewService.deleteReview(userId, reviewId);
 return ResponseResult.success(result);
 }

 /**
 * 获取评价统计
 */
 @GetMapping("/statistics/{productId}")
 public ResponseResult<ReviewService.ReviewStatisticsDTO> getReviewStatistics( @PathVariable Long productId) {
 ReviewService.ReviewStatisticsDTO statistics = reviewService.getReviewStatistics(productId);
 return ResponseResult.success(statistics);
 }
}
