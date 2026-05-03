package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.entity.DiscountActivity;
import org.example.shoppingserver.model.entity.SeckillActivity;
import org.example.shoppingserver.service.MerchantMarketingService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 商家营销中心控制器
 *
 * @author System
 * @since 2026-04-28
 */
@RestController
@RequestMapping("/merchant/marketing")
@RequiredArgsConstructor
public class MerchantMarketingController {

    private final MerchantMarketingService merchantMarketingService;

    // ==================== 秒杀活动管理 ====================

    /**
     * 创建秒杀活动
     *
     * @param merchantId 商家ID
     * @param dto 秒杀活动信息
     * @return 活动ID
     */
    @PostMapping("/seckill")
    public ResponseResult<Long> createSeckillActivity(
            @RequestParam Long merchantId,
            @RequestBody MerchantMarketingService.SeckillActivityDTO dto) {
        Long activityId = merchantMarketingService.createSeckillActivity(merchantId, dto);
        return ResponseResult.success(activityId);
    }

    /**
     * 更新秒杀活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param dto 秒杀活动信息
     * @return 操作结果
     */
    @PutMapping("/seckill/{activityId}")
    public ResponseResult<Void> updateSeckillActivity(
            @RequestParam Long merchantId,
            @PathVariable Long activityId,
            @RequestBody MerchantMarketingService.SeckillActivityDTO dto) {
        merchantMarketingService.updateSeckillActivity(merchantId, activityId, dto);
        return ResponseResult.success();
    }

    /**
     * 删除秒杀活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 操作结果
     */
    @DeleteMapping("/seckill/{activityId}")
    public ResponseResult<Void> deleteSeckillActivity(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        merchantMarketingService.deleteSeckillActivity(merchantId, activityId);
        return ResponseResult.success();
    }

    /**
     * 获取秒杀活动列表
     *
     * @param merchantId 商家ID
     * @param status 活动状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 秒杀活动分页列表
     */
    @GetMapping("/seckill")
    public ResponseResult<Page<SeckillActivity>> getSeckillActivities(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SeckillActivity> page = merchantMarketingService.getSeckillActivities(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 获取秒杀活动详情
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 秒杀活动详情
     */
    @GetMapping("/seckill/{activityId}")
    public ResponseResult<SeckillActivity> getSeckillActivityDetail(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        SeckillActivity activity = merchantMarketingService.getSeckillActivityDetail(merchantId, activityId);
        return ResponseResult.success(activity);
    }

    /**
     * 更新秒杀活动状态
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param status 目标状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @return 操作结果
     */
    @PutMapping("/seckill/{activityId}/status")
    public ResponseResult<Void> updateSeckillStatus(
            @RequestParam Long merchantId,
            @PathVariable Long activityId,
            @RequestParam Integer status) {
        merchantMarketingService.updateSeckillStatus(merchantId, activityId, status);
        return ResponseResult.success();
    }

    // ==================== 满减活动管理 ====================

    /**
     * 创建满减活动
     *
     * @param merchantId 商家ID
     * @param dto 满减活动信息
     * @return 活动ID
     */
    @PostMapping("/discount")
    public ResponseResult<Long> createDiscountActivity(
            @RequestParam Long merchantId,
            @RequestBody MerchantMarketingService.DiscountActivityDTO dto) {
        Long activityId = merchantMarketingService.createDiscountActivity(merchantId, dto);
        return ResponseResult.success(activityId);
    }

    /**
     * 更新满减活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param dto 满减活动信息
     * @return 操作结果
     */
    @PutMapping("/discount/{activityId}")
    public ResponseResult<Void> updateDiscountActivity(
            @RequestParam Long merchantId,
            @PathVariable Long activityId,
            @RequestBody MerchantMarketingService.DiscountActivityDTO dto) {
        merchantMarketingService.updateDiscountActivity(merchantId, activityId, dto);
        return ResponseResult.success();
    }

    /**
     * 删除满减活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 操作结果
     */
    @DeleteMapping("/discount/{activityId}")
    public ResponseResult<Void> deleteDiscountActivity(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        merchantMarketingService.deleteDiscountActivity(merchantId, activityId);
        return ResponseResult.success();
    }

    /**
     * 获取满减活动列表
     *
     * @param merchantId 商家ID
     * @param status 活动状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 满减活动分页列表
     */
    @GetMapping("/discount")
    public ResponseResult<Page<DiscountActivity>> getDiscountActivities(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<DiscountActivity> page = merchantMarketingService.getDiscountActivities(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 获取满减活动详情
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 满减活动详情
     */
    @GetMapping("/discount/{activityId}")
    public ResponseResult<DiscountActivity> getDiscountActivityDetail(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        DiscountActivity activity = merchantMarketingService.getDiscountActivityDetail(merchantId, activityId);
        return ResponseResult.success(activity);
    }

    /**
     * 更新满减活动状态
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param status 目标状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @return 操作结果
     */
    @PutMapping("/discount/{activityId}/status")
    public ResponseResult<Void> updateDiscountStatus(
            @RequestParam Long merchantId,
            @PathVariable Long activityId,
            @RequestParam Integer status) {
        merchantMarketingService.updateDiscountStatus(merchantId, activityId, status);
        return ResponseResult.success();
    }

    // ==================== 优惠券管理（商家端）====================

    /**
     * 创建优惠券
     *
     * @param merchantId 商家ID
     * @param dto 优惠券创建信息
     * @return 优惠券ID
     */
    @PostMapping("/coupon")
    public ResponseResult<Long> createCoupon(
            @RequestParam Long merchantId,
            @RequestBody MerchantMarketingService.CouponCreateDTO dto) {
        Long couponId = merchantMarketingService.createCoupon(merchantId, dto);
        return ResponseResult.success(couponId);
    }

    /**
     * 更新优惠券
     *
     * @param merchantId 商家ID
     * @param couponId 优惠券ID
     * @param dto 优惠券更新信息
     * @return 操作结果
     */
    @PutMapping("/coupon/{couponId}")
    public ResponseResult<Void> updateCoupon(
            @RequestParam Long merchantId,
            @PathVariable Long couponId,
            @RequestBody MerchantMarketingService.CouponUpdateDTO dto) {
        merchantMarketingService.updateCoupon(merchantId, couponId, dto);
        return ResponseResult.success();
    }

    /**
     * 删除优惠券
     *
     * @param merchantId 商家ID
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    @DeleteMapping("/coupon/{couponId}")
    public ResponseResult<Void> deleteCoupon(
            @RequestParam Long merchantId,
            @PathVariable Long couponId) {
        merchantMarketingService.deleteCoupon(merchantId, couponId);
        return ResponseResult.success();
    }

    /**
     * 获取商家优惠券列表
     *
     * @param merchantId 商家ID
     * @param status 优惠券状态（1-可用，2-已停用）
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 优惠券分页列表
     */
    @GetMapping("/coupon")
    public ResponseResult<Page<org.example.shoppingserver.model.entity.Coupon>> getMerchantCoupons(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<org.example.shoppingserver.model.entity.Coupon> page = 
                merchantMarketingService.getMerchantCoupons(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 获取优惠券统计信息
     *
     * @param merchantId 商家ID
     * @return 优惠券统计数据（总数、激活数、领取数、使用数）
     */
    @GetMapping("/coupon/statistics")
    public ResponseResult<MerchantMarketingService.CouponStatistics> getCouponStatistics(
            @RequestParam Long merchantId) {
        MerchantMarketingService.CouponStatistics statistics = 
                merchantMarketingService.getCouponStatistics(merchantId);
        return ResponseResult.success(statistics);
    }
}
