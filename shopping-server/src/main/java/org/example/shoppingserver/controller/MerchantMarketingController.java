package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.coupon.CouponCreateDTO;
import org.example.shoppingserver.model.vo.coupon.CouponStatisticsVO;
import org.example.shoppingserver.model.dto.coupon.CouponUpdateDTO;
import org.example.shoppingserver.model.dto.coupon.MerchantCouponQueryDTO;
import org.example.shoppingserver.model.dto.marketing.DiscountActivityDTO;
import org.example.shoppingserver.model.dto.marketing.DiscountActivityQueryDTO;
import org.example.shoppingserver.model.dto.marketing.SeckillActivityDTO;
import org.example.shoppingserver.model.dto.marketing.SeckillActivityQueryDTO;
import org.example.shoppingserver.model.vo.marketing.DiscountActivityVO;
import org.example.shoppingserver.model.vo.marketing.SeckillActivityVO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.service.MerchantMarketingService;
import org.example.shoppingserver.util.annotation.RequireRole;
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
@RequireRole(value = {"ROLE_MERCHANT"})
public class MerchantMarketingController {

    private final MerchantMarketingService merchantMarketingService;

    // ==================== 秒杀活动管理 ====================

    /**
     * 创建秒杀活动
     *
     * @param dto 秒杀活动信息
     * @return 活动ID
     */
    @PostMapping("/seckill")
    public ResponseResult<Long> createSeckillActivity(
            @RequestBody SeckillActivityDTO dto) {
        Long activityId = merchantMarketingService.createSeckillActivity(dto.getMerchantId(), dto);
        return ResponseResult.success(activityId);
    }

    /**
     * 更新秒杀活动
     *
     * @param activityId 活动ID
     * @param dto 秒杀活动信息
     * @return 操作结果
     */
    @PutMapping("/seckill/{activityId}")
    public ResponseResult<Void> updateSeckillActivity(
            @PathVariable Long activityId,
            @RequestBody SeckillActivityDTO dto) {
        merchantMarketingService.updateSeckillActivity(dto.getMerchantId(), activityId, dto);
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
     * @param queryDTO 查询条件
     * @return 秒杀活动分页列表
     */
    @GetMapping("/seckill")
    public ResponseResult<Page<SeckillActivityVO>> getSeckillActivities(
            @RequestParam Long merchantId,
            @ModelAttribute SeckillActivityQueryDTO queryDTO) {
        Page<SeckillActivityVO> page = merchantMarketingService.getSeckillActivities(
                merchantId, 
                queryDTO.getStatus(), 
                queryDTO.getPageNum(), 
                queryDTO.getPageSize());
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
    public ResponseResult<SeckillActivityVO> getSeckillActivityDetail(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        SeckillActivityVO activity = merchantMarketingService.getSeckillActivityDetail(merchantId, activityId);
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
     * @param dto 满减活动信息
     * @return 活动ID
     */
    @PostMapping("/discount")
    public ResponseResult<Long> createDiscountActivity(
            @RequestBody DiscountActivityDTO dto) {
        Long activityId = merchantMarketingService.createDiscountActivity(dto.getMerchantId(), dto);
        return ResponseResult.success(activityId);
    }

    /**
     * 更新满减活动
     *
     * @param dto 满减活动信息
     * @return 操作结果
     */
    @PutMapping("/discount")
    public ResponseResult<Void> updateDiscountActivity(
            @RequestBody DiscountActivityDTO dto) {
        merchantMarketingService.updateDiscountActivity(dto.getMerchantId(), dto.getActivityId(), dto);
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
     * @param queryDTO 查询条件
     * @return 满减活动分页列表
     */
    @GetMapping("/discount")
    public ResponseResult<Page<DiscountActivityVO>> getDiscountActivities(
            @ModelAttribute DiscountActivityQueryDTO queryDTO) {
        Page<DiscountActivityVO> page = merchantMarketingService.getDiscountActivities(
                queryDTO.getMerchantId(),
                queryDTO.getStatus(), 
                queryDTO.getPageNum(), 
                queryDTO.getPageSize());
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
    public ResponseResult<DiscountActivityVO> getDiscountActivityDetail(
            @RequestParam Long merchantId,
            @PathVariable Long activityId) {
        DiscountActivityVO activity = merchantMarketingService.getDiscountActivityDetail(merchantId, activityId);
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
     * @param dto 优惠券创建信息
     * @return 优惠券ID
     */
    @PostMapping("/coupon")
    public ResponseResult<Long> createCoupon(
            @RequestBody CouponCreateDTO dto) {
        Long couponId = merchantMarketingService.createCoupon(UserHolder.getCurrentUserId(), dto);
        return ResponseResult.success(couponId);
    }

    /**
     * 更新优惠券
     *
     * @param couponId 优惠券ID
     * @param dto 优惠券更新信息
     * @return 操作结果
     */
    @PutMapping("/coupon/{couponId}")
    public ResponseResult<Void> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody CouponUpdateDTO dto) {
        merchantMarketingService.updateCoupon(dto.getMerchantId(), couponId, dto);
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
     * @param queryDTO 查询条件
     * @return 优惠券分页列表
     */
    @GetMapping("/coupon")
    public ResponseResult<Page<CouponVO>> getMerchantCoupons(
            @ModelAttribute MerchantCouponQueryDTO queryDTO) {
        Page<CouponVO> page = merchantMarketingService.getMerchantCoupons(
                queryDTO.getMerchantId(),
                queryDTO.getStatus(), 
                queryDTO.getPageNum(), 
                queryDTO.getPageSize());
        return ResponseResult.success(page);
    }

    /**
     * 获取优惠券统计信息
     *
     * @param merchantId 商家ID
     * @return 优惠券统计数据（总数、激活数、领取数、使用数）
     */
    @GetMapping("/coupon/statistics")
    public ResponseResult<CouponStatisticsVO> getCouponStatistics(
            @RequestParam Long merchantId) {
        CouponStatisticsVO statistics = merchantMarketingService.getCouponStatistics(merchantId);
        return ResponseResult.success(statistics);
    }
}
