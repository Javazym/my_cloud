package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.coupon.CouponCreateDTO;
import org.example.shoppingserver.model.vo.coupon.CouponStatisticsVO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.model.dto.coupon.CouponUpdateDTO;
import org.example.shoppingserver.model.dto.marketing.DiscountActivityDTO;
import org.example.shoppingserver.model.dto.marketing.SeckillActivityDTO;
import org.example.shoppingserver.model.vo.marketing.DiscountActivityVO;
import org.example.shoppingserver.model.vo.marketing.SeckillActivityVO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * 商家营销服务接口
 */
public interface MerchantMarketingService {

    // ==================== 秒杀活动管理 ====================

    /**
     * 创建秒杀活动
     */
    Long createSeckillActivity(Long merchantId, SeckillActivityDTO dto);

    /**
     * 更新秒杀活动
     */
    void updateSeckillActivity(Long merchantId, Long activityId, SeckillActivityDTO dto);

    /**
     * 删除秒杀活动
     */
    void deleteSeckillActivity(Long merchantId, Long activityId);

    /**
     * 获取秒杀活动列表
     */
    Page<SeckillActivityVO> getSeckillActivities(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取秒杀活动详情
     */
    SeckillActivityVO getSeckillActivityDetail(Long merchantId, Long activityId);

    /**
     * 更新秒杀活动状态
     */
    void updateSeckillStatus(Long merchantId, Long activityId, Integer status);

    /**
     * 抢购秒杀商品
     */
    boolean seckillProduct(String userId, Long activityId);

    // ==================== 满减活动管理 ====================

    /**
     * 创建满减活动
     */
    Long createDiscountActivity(Long merchantId, DiscountActivityDTO dto);

    /**
     * 更新满减活动
     */
    void updateDiscountActivity(Long merchantId, Long activityId, DiscountActivityDTO dto);

    /**
     * 删除满减活动
     */
    void deleteDiscountActivity(Long merchantId, Long activityId);

    /**
     * 获取满减活动列表
     */
    Page<DiscountActivityVO> getDiscountActivities(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取满减活动详情
     */
    DiscountActivityVO getDiscountActivityDetail(Long merchantId, Long activityId);

    /**
     * 更新满减活动状态
     */
    void updateDiscountStatus(Long merchantId, Long activityId, Integer status);

    /**
     * 计算满减优惠
     */
    BigDecimal calculateDiscount(Long merchantId, BigDecimal orderAmount, Integer quantity);

    // ==================== 优惠券管理（商家端）====================

    /**
     * 创建优惠券
     */
    Long createCoupon(String merchantId, CouponCreateDTO dto);

    /**
     * 更新优惠券
     */
    void updateCoupon(Long merchantId, Long couponId, CouponUpdateDTO dto);

    /**
     * 删除优惠券
     */
    void deleteCoupon(Long merchantId, Long couponId);

    /**
     * 获取商家优惠券列表
     */
    Page<CouponVO> getMerchantCoupons(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取优惠券统计
     */
    CouponStatisticsVO getCouponStatistics(Long merchantId);

    // ==================== DTO 定义 ====================

}
