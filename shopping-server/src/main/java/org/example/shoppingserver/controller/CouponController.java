package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.entity.Coupon;
import org.example.shoppingserver.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {


 private final CouponService couponService;

 /**
  * 获取优惠券列表
  */
 @GetMapping
 public ResponseResult<Page<Coupon>> getCoupons(@RequestParam(required = false) Long merchantId, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
  CouponService.CouponQueryDTO queryDTO = new CouponService.CouponQueryDTO();
  queryDTO.setMerchantId(merchantId);
  queryDTO.setStatus(status);
  queryDTO.setPageNum(pageNum);
  queryDTO.setPageSize(pageSize);
  Page<Coupon> page = couponService.getCoupons(queryDTO);
  return ResponseResult.success(page);
 }

 /**
  * 获取可用优惠券
  */
 @GetMapping("/available")
 public ResponseResult<List<Coupon>> getAvailableCoupons(@RequestParam String userId, @RequestParam BigDecimal orderAmount) {
  List<Coupon> coupons = couponService.getAvailableCoupons(userId, orderAmount);
  return ResponseResult.success(coupons);
 }

 /**
  * 获取优惠券详情
  */
 @GetMapping("/{couponId}")
 public ResponseResult<Coupon> getCouponDetail(@PathVariable Long couponId) {
  Coupon coupon = couponService.getCouponDetail(couponId);
  return ResponseResult.success(coupon);
 }

 /**
  * 领取优惠券
  */
 @PostMapping("/{couponId}/receive")
 public ResponseResult<Boolean> receiveCoupon(@RequestParam String userId, @PathVariable Long couponId) {
  boolean result = couponService.receiveCoupon(userId, couponId);
  return ResponseResult.success(result);
 }

 /**
  * 获取用户优惠券
  */
 @GetMapping("/my")
 public ResponseResult<Page<CouponService.UserCouponDTO>> getUserCoupons(@RequestParam String userId, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
  Page<CouponService.UserCouponDTO> page = couponService.getUserCoupons(userId, status, pageNum, pageSize);
  return ResponseResult.success(page);
 }

 /**
  * 验证优惠券
  */
 @PostMapping("/validate")
 public ResponseResult<CouponService.ValidateResultDTO> validateCoupon(@RequestParam String userId, @RequestParam Long couponId, @RequestParam BigDecimal orderAmount) {
  CouponService.ValidateResultDTO result = couponService.validateCoupon(userId, couponId, orderAmount);
  return ResponseResult.success(result);
 }

 /**
  * 使用优惠券
  */
 @PostMapping("/{couponId}/use")
 public ResponseResult<Boolean> useCoupon(@RequestParam String userId, @PathVariable Long couponId,
                                          @RequestBody Map<String, Long> request) {
  Long orderId = request.get("orderId");
  boolean result = couponService.useCoupon(userId, couponId, orderId);
  return ResponseResult.success(result);
 }
}
