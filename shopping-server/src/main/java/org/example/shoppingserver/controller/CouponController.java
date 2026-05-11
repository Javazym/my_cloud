package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.coupon.CouponQueryDTO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.model.vo.coupon.UserCouponVO;
import org.example.shoppingserver.model.vo.coupon.ValidateResultVO;
import org.example.shoppingserver.service.CouponService;
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
 public ResponseResult<List<CouponVO>> getCoupons(@RequestParam(required = false) Long merchantId,
                                                  @RequestParam(required = false) Integer status) {
  CouponQueryDTO queryDTO = new CouponQueryDTO();
  queryDTO.setMerchantId(merchantId);
  queryDTO.setStatus(status);
  List<CouponVO> coupons = couponService.getCoupons(queryDTO);
  return ResponseResult.success(coupons);
 }

 /**
  * 获取可用优惠券
  */
 @GetMapping("/available")
 public ResponseResult<List<CouponVO>> getAvailableCoupons(@RequestParam BigDecimal orderAmount) {
  List<CouponVO> coupons = couponService.getAvailableCoupons(UserHolder.getCurrentUserId(), orderAmount);
  return ResponseResult.success(coupons);
 }

 /**
  * 获取优惠券详情
  */
 @GetMapping("/{couponId}")
 public ResponseResult<CouponVO> getCouponDetail(@PathVariable Long couponId) {
  CouponVO coupon = couponService.getCouponDetail(couponId);
  return ResponseResult.success(coupon);
 }

 /**
  * 领取优惠券
  */
 @PostMapping("/{couponId}/receive")
 public ResponseResult<Boolean> receiveCoupon(@PathVariable Long couponId) {
  boolean result = couponService.receiveCoupon(UserHolder.getCurrentUserId(), couponId);
  return ResponseResult.success(result);
 }

 /**
  * 获取用户优惠券
  */
 @GetMapping("/my")
 public ResponseResult<Page<UserCouponVO>> getUserCoupons(@RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
  Page<UserCouponVO> page = couponService.getUserCoupons(UserHolder.getCurrentUserId(), status, pageNum, pageSize);
  return ResponseResult.success(page);
 }

 /**
  * 验证优惠券
  */
 @PostMapping("/validate")
 public ResponseResult<ValidateResultVO> validateCoupon(@RequestParam Long couponId, @RequestParam BigDecimal orderAmount) {
  ValidateResultVO result = couponService.validateCoupon(UserHolder.getCurrentUserId(), couponId, orderAmount);
  return ResponseResult.success(result);
 }

 /**
  * 使用优惠券
  */
 @PostMapping("/{couponId}/use")
 public ResponseResult<Boolean> useCoupon(@PathVariable Long couponId,
                                          @RequestBody Map<String, Long> request) {
  Long orderId = request.get("orderId");
  boolean result = couponService.useCoupon(UserHolder.getCurrentUserId(), couponId, orderId);
  return ResponseResult.success(result);
 }

 /**
  * 获取指定商品的可用优惠券
  */
 @GetMapping("/product/{productId}")
 public ResponseResult<List<CouponVO>> getAvailableCouponsForProduct(
         @PathVariable Long productId,
         @RequestParam Long merchantId) {
  List<CouponVO> coupons = couponService.getAvailableCouponsForProduct(productId, merchantId);
  return ResponseResult.success(coupons);
 }

 /**
  * 获取用户可用于指定商品的优惠券
  */
 @GetMapping("/user/product/{productId}")
 public ResponseResult<List<UserCouponVO>> getUserAvailableCouponsForProduct(
         @PathVariable Long productId,
         @RequestParam Long merchantId) {
  List<UserCouponVO> coupons = couponService.getUserAvailableCouponsForProduct(UserHolder.getCurrentUserId(), productId, merchantId);
  return ResponseResult.success(coupons);
 }
}
