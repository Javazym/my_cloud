package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.dto.coupon.CouponCreateDTO;
import org.example.shoppingserver.model.vo.coupon.CouponStatisticsVO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.model.dto.coupon.CouponUpdateDTO;
import org.example.shoppingserver.model.dto.marketing.DiscountActivityDTO;
import org.example.shoppingserver.model.dto.marketing.SeckillActivityDTO;
import org.example.shoppingserver.model.entity.coupon.Coupon;
import org.example.shoppingserver.model.entity.marketing.DiscountActivity;
import org.example.shoppingserver.model.entity.marketing.SeckillActivity;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.repository.*;
import org.example.shoppingserver.service.MerchantMarketingService;
import org.example.shoppingserver.model.vo.marketing.DiscountActivityVO;
import org.example.shoppingserver.model.vo.marketing.SeckillActivityVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家营销服务实现类
 *
 * @author System
 * @since 2026-04-28
 */
@Service
@RequiredArgsConstructor
public class MerchantMarketingServiceImpl implements MerchantMarketingService {

    private final SeckillActivityRepository seckillActivityRepository;
    private final DiscountActivityRepository discountActivityRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;

    /**
     * 创建秒杀活动
     *
     * @param merchantId 商家ID
     * @param dto 秒杀活动信息
     * @return 活动ID
     */
    @Override
    @Transactional
    public Long createSeckillActivity(Long merchantId, SeckillActivityDTO dto) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        // 验证商品价格
        if (dto.getSeckillPrice().compareTo(dto.getOriginalPrice()) >= 0) {
            throw new RuntimeException("秒杀价格必须低于原价");
        }

        SeckillActivity activity = new SeckillActivity();
        activity.setMerchant(merchant);
        activity.setName(dto.getName());
        activity.setProductId(dto.getProductId());
        activity.setSkuId(dto.getSkuId());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setOriginalPrice(dto.getOriginalPrice());
        activity.setStock(dto.getStock());
        activity.setSoldCount(0);
        activity.setLimitPerUser(dto.getLimitPerUser() != null ? dto.getLimitPerUser() : 1);
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(0);
        activity.setSort(dto.getSort() != null ? dto.getSort() : 0);

        return seckillActivityRepository.save(activity).getId();
    }

    /**
     * 更新秒杀活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param dto 秒杀活动信息
     */
    @Override
    @Transactional
    public void updateSeckillActivity(Long merchantId, Long activityId, SeckillActivityDTO dto) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        activity.setName(dto.getName());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setOriginalPrice(dto.getOriginalPrice());
        activity.setStock(dto.getStock());
        activity.setLimitPerUser(dto.getLimitPerUser());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setSort(dto.getSort());

        seckillActivityRepository.save(activity);
    }

    /**
     * 删除秒杀活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     */
    @Override
    @Transactional
    public void deleteSeckillActivity(Long merchantId, Long activityId) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        seckillActivityRepository.delete(activity);
    }

    /**
     * 获取秒杀活动列表
     *
     * @param merchantId 商家ID
     * @param status 活动状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 秒杀活动分页列表
     */
    @Override
    public Page<SeckillActivityVO> getSeckillActivities(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        Page<SeckillActivity> page;
        if (status == null) {
            page = seckillActivityRepository.findByMerchantId(merchantId, pageable);
        } else {
            page = seckillActivityRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
        }
        
        return page.map(this::convertToSeckillVO);
    }

    /**
     * 获取秒杀活动详情
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 秒杀活动详情
     */
    @Override
    public SeckillActivityVO getSeckillActivityDetail(Long merchantId, Long activityId) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权查看此活动");
        }

        return convertToSeckillVO(activity);
    }

    /**
     * 更新秒杀活动状态
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param status 目标状态
     */
    @Override
    @Transactional
    public void updateSeckillStatus(Long merchantId, Long activityId, Integer status) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        activity.setStatus(status);
        seckillActivityRepository.save(activity);
    }

    /**
     * 抢购秒杀商品
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 是否抢购成功
     */
    @Override
    @Transactional
    public boolean seckillProduct(String userId, Long activityId) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        if (!activity.isValid()) {
            return false;
        }

        // TODO: 检查用户限购
        // TODO: 扣减库存（需要使用乐观锁或分布式锁）
        activity.setSoldCount(activity.getSoldCount() + 1);
        seckillActivityRepository.save(activity);

        return true;
    }

    // ==================== 满减活动管理 ====================

    /**
     * 创建满减活动
     *
     * @param merchantId 商家ID
     * @param dto 满减活动信息
     * @return 活动ID
     */
    @Override
    @Transactional
    public Long createDiscountActivity(Long merchantId, DiscountActivityDTO dto) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        DiscountActivity activity = new DiscountActivity();
        activity.setMerchant(merchant);
        activity.setName(dto.getName());
        activity.setDescription(dto.getDescription());
        activity.setDiscountType(dto.getDiscountType());
        activity.setConditionValue(dto.getConditionValue());
        activity.setDiscountAmount(dto.getDiscountAmount());
        activity.setMaxDiscount(dto.getMaxDiscount());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(0);
        activity.setScopeType(dto.getScopeType());
        activity.setScopeIds(dto.getScopeIds());
        activity.setLimitPerUser(dto.getLimitPerUser());
        activity.setUsedCount(0);
        activity.setSort(dto.getSort() != null ? dto.getSort() : 0);

        return discountActivityRepository.save(activity).getId();
    }

    /**
     * 更新满减活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param dto 满减活动信息
     */
    @Override
    @Transactional
    public void updateDiscountActivity(Long merchantId, Long activityId, DiscountActivityDTO dto) {
        DiscountActivity activity = discountActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("满减活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        activity.setName(dto.getName());
        activity.setDescription(dto.getDescription());
        activity.setDiscountAmount(dto.getDiscountAmount());
        activity.setMaxDiscount(dto.getMaxDiscount());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setScopeType(dto.getScopeType());
        activity.setScopeIds(dto.getScopeIds());
        activity.setLimitPerUser(dto.getLimitPerUser());
        activity.setSort(dto.getSort());

        discountActivityRepository.save(activity);
    }

    /**
     * 删除满减活动
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     */
    @Override
    @Transactional
    public void deleteDiscountActivity(Long merchantId, Long activityId) {
        DiscountActivity activity = discountActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("满减活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        discountActivityRepository.delete(activity);
    }

    /**
     * 获取满减活动列表
     *
     * @param merchantId 商家ID
     * @param status 活动状态（0-未开始，1-进行中，2-已结束，3-已取消）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 满减活动分页列表
     */
    @Override
    public Page<DiscountActivityVO> getDiscountActivities(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        Page<DiscountActivity> page;
        if (status == null) {
            page = discountActivityRepository.findByMerchantId(merchantId, pageable);
        } else {
            page = discountActivityRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
        }
        
        return page.map(this::convertToDiscountVO);
    }

    /**
     * 获取满减活动详情
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @return 满减活动详情
     */
    @Override
    public DiscountActivityVO getDiscountActivityDetail(Long merchantId, Long activityId) {
        DiscountActivity activity = discountActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("满减活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权查看此活动");
        }

        return convertToDiscountVO(activity);
    }

    /**
     * 更新满减活动状态
     *
     * @param merchantId 商家ID
     * @param activityId 活动ID
     * @param status 目标状态
     */
    @Override
    @Transactional
    public void updateDiscountStatus(Long merchantId, Long activityId, Integer status) {
        DiscountActivity activity = discountActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("满减活动不存在"));

        if (!activity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此活动");
        }

        activity.setStatus(status);
        discountActivityRepository.save(activity);
    }

    /**
     * 计算满减优惠金额
     *
     * @param merchantId 商家ID
     * @param orderAmount 订单金额
     * @param quantity 商品数量
     * @return 优惠金额
     */
    @Override
    public BigDecimal calculateDiscount(Long merchantId, BigDecimal orderAmount, Integer quantity) {
        LocalDateTime now = LocalDateTime.now();
        List<DiscountActivity> activities = discountActivityRepository
                .findByStatusAndStartTimeBeforeAndEndTimeAfter(1, now, now);

        BigDecimal maxDiscount = BigDecimal.ZERO;

        for (DiscountActivity activity : activities) {
            if (!activity.getMerchant().getId().equals(merchantId)) {
                continue;
            }

            BigDecimal discount = BigDecimal.ZERO;

            // 满额减
            if (activity.getDiscountType() == 2 && orderAmount.compareTo(activity.getConditionValue()) >= 0) {
                discount = activity.getDiscountAmount();
            }
            // 满件减
            else if (activity.getDiscountType() == 1 && quantity >= activity.getConditionValue().intValue()) {
                discount = activity.getDiscountAmount();
            }

            // 应用封顶
            if (activity.getMaxDiscount() != null && discount.compareTo(activity.getMaxDiscount()) > 0) {
                discount = activity.getMaxDiscount();
            }

            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
            }
        }

        return maxDiscount;
    }

    // ==================== 优惠券管理（商家端）====================

    /**
     * 创建优惠券
     *
     * @param merchantId 商家ID
     * @param dto 优惠券创建信息
     * @return 优惠券ID
     */
    @Override
    @Transactional
    public Long createCoupon(String merchantId, CouponCreateDTO dto) {
        Merchant merchant = merchantRepository.findByUserId(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        Coupon coupon = new Coupon();
        coupon.setMerchant(merchant);
        coupon.setName(dto.getName());
        coupon.setType(dto.getType());
        coupon.setValue(dto.getValue());
        coupon.setMinAmount(dto.getMinAmount());
        coupon.setMaxDiscount(dto.getMaxDiscount());
        coupon.setTotalCount(dto.getTotalStock());
        coupon.setReceiveCount(0);
        coupon.setUsedCount(0);
        coupon.setLimitPerUser(dto.getLimitPerUser());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        coupon.setStatus(1);

        return couponRepository.save(coupon).getId();
    }

    /**
     * 更新优惠券
     *
     * @param merchantId 商家ID
     * @param couponId 优惠券ID
     * @param dto 优惠券更新信息
     */
    @Override
    @Transactional
    public void updateCoupon(Long merchantId, Long couponId, CouponUpdateDTO dto) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (!coupon.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此优惠券");
        }

        coupon.setName(dto.getName());
        coupon.setValue(dto.getValue());
        coupon.setMinAmount(dto.getMinAmount());
        coupon.setMaxDiscount(dto.getMaxDiscount());
        coupon.setTotalCount(dto.getTotalStock());
        coupon.setLimitPerUser(dto.getLimitPerUser());
        coupon.setEndTime(dto.getEndTime());
        coupon.setStatus(dto.getStatus());

        couponRepository.save(coupon);
    }

    /**
     * 删除优惠券
     *
     * @param merchantId 商家ID
     * @param couponId 优惠券ID
     */
    @Override
    @Transactional
    public void deleteCoupon(Long merchantId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (!coupon.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此优惠券");
        }

        couponRepository.delete(coupon);
    }

    /**
     * 获取商家优惠券列表
     *
     * @param merchantId 商家ID
     * @param status 优惠券状态（1-可用，2-已停用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 优惠券分页列表
     */
    @Override
    public Page<CouponVO> getMerchantCoupons(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        Page<Coupon> page;
        if (status == null) {
            page = couponRepository.findByMerchantId(merchantId, pageable);
        } else {
            page = couponRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
        }
        
        return page.map(this::convertToCouponVO);
    }

    /**
     * 获取优惠券统计信息
     *
     * @param merchantId 商家ID
     * @return 优惠券统计数据
     */
    @Override
    public CouponStatisticsVO getCouponStatistics(Long merchantId) {
        CouponStatisticsVO statistics = new CouponStatisticsVO();
        
        // 总优惠券数
        statistics.setTotalCoupons(couponRepository.countByMerchantId(merchantId));
        
        // 进行中的优惠券数
        statistics.setActiveCoupons(couponRepository.countByMerchantIdAndStatus(merchantId, 1));
        
        // 总领取数和使用数需要从 UserCoupon 统计
        statistics.setTotalReceived(userCouponRepository.countByCouponMerchantId(merchantId));
        statistics.setTotalUsed(userCouponRepository.countByCouponMerchantIdAndStatus(merchantId, 1));

        return statistics;
    }

    // ==================== 转换方法 ====================

    /**
     * 转换 SeckillActivity 到 SeckillActivityVO
     */
    private SeckillActivityVO convertToSeckillVO(SeckillActivity activity) {
        SeckillActivityVO vo = new SeckillActivityVO();
        vo.setId(activity.getId());
        vo.setMerchantId(activity.getMerchant().getId());
        vo.setName(activity.getName());
        vo.setProductId(activity.getProductId());
        vo.setSkuId(activity.getSkuId());
        vo.setSeckillPrice(activity.getSeckillPrice());
        vo.setOriginalPrice(activity.getOriginalPrice());
        vo.setStock(activity.getStock());
        vo.setSoldCount(activity.getSoldCount());
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setSort(activity.getSort());
        return vo;
    }

    /**
     * 转换 DiscountActivity 到 DiscountActivityVO
     */
    private DiscountActivityVO convertToDiscountVO(DiscountActivity activity) {
        DiscountActivityVO vo = new DiscountActivityVO();
        vo.setId(activity.getId());
        vo.setMerchantId(activity.getMerchant().getId());
        vo.setName(activity.getName());
        vo.setDescription(activity.getDescription());
        vo.setDiscountType(activity.getDiscountType());
        vo.setConditionValue(activity.getConditionValue());
        vo.setDiscountAmount(activity.getDiscountAmount());
        vo.setMaxDiscount(activity.getMaxDiscount());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setScopeType(activity.getScopeType());
        vo.setScopeIds(activity.getScopeIds());
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setUsedCount(activity.getUsedCount());
        vo.setSort(activity.getSort());
        return vo;
    }

    /**
     * 转换 Coupon 到 CouponVO
     */
    private CouponVO convertToCouponVO(Coupon coupon) {
        CouponVO vo = new CouponVO();
        vo.setId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount());
        vo.setMaxDiscount(coupon.getMaxDiscount());
        vo.setTotalCount(coupon.getTotalCount());
        vo.setReceiveCount(coupon.getReceiveCount());
        vo.setUsedCount(coupon.getUsedCount());
        vo.setLimitPerUser(coupon.getLimitPerUser());
        vo.setValidDays(coupon.getValidDays());
        vo.setStartTime(coupon.getStartTime());
        vo.setEndTime(coupon.getEndTime());
        vo.setStatus(coupon.getStatus());
        if (coupon.getMerchant() != null) {
            vo.setMerchantId(coupon.getMerchant().getId());
        }
        return vo;
    }
}
