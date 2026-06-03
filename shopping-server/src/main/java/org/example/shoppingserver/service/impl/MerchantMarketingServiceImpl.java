package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.shoppingserver.model.entity.product.Product;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 商家营销服务实现类
 *
 * @author System
 * @since 2026-04-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantMarketingServiceImpl implements MerchantMarketingService {

    private final SeckillActivityRepository seckillActivityRepository;
    private final DiscountActivityRepository discountActivityRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final org.example.shoppingserver.util.SeckillStockUtil seckillStockUtil;
    private final org.example.shoppingserver.task.SeckillInventoryInitializer seckillInventoryInitializer;

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

        // 验证商品是否已经有进行中的秒杀活动（一对一关系）
        List<SeckillActivity> existingActivities = seckillActivityRepository.findByProduct_IdAndStatusIn(
            dto.getProductId(), java.util.Arrays.asList(0, 1)); // 0-未开始，1-进行中
        if (!existingActivities.isEmpty()) {
            throw new RuntimeException("该商品已经参与了秒杀活动，请等待活动结束后再创建新的活动");
        }

        SeckillActivity activity = new SeckillActivity();
        activity.setMerchant(merchant);
        
        // 查询并设置商品关联
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        activity.setProduct(product);
        
        // 秒杀活动不需要指定SKU，库存会均分给商品的所有SKU
        activity.setSkuId(null);
        activity.setName(dto.getName());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setOriginalPrice(dto.getOriginalPrice());
        activity.setStock(dto.getStock());
        activity.setSoldCount(0);
        activity.setLimitPerUser(dto.getLimitPerUser() != null ? dto.getLimitPerUser() : 1);
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(0);
        activity.setSort(dto.getSort() != null ? dto.getSort() : 0);

        // 验证库存是否大于0
        if (dto.getStock() == null || dto.getStock() <= 0) {
            throw new RuntimeException("秒杀库存必须大于0");
        }

        // 验证商品是否有SKU
        if (product.getSkus() == null || product.getSkus().isEmpty()) {
            throw new RuntimeException("商品没有可用的SKU，无法创建秒杀活动");
        }

        SeckillActivity savedActivity = seckillActivityRepository.save(activity);
        
        // 预热库存到Redis
        try {
            seckillInventoryInitializer.preloadActivityStock(savedActivity.getId());
            log.info("秒杀活动库存预热成功: activityId={}, stock={}", savedActivity.getId(), savedActivity.getStock());
        } catch (Exception e) {
            log.error("秒杀活动库存预热失败: activityId={}, error={}", savedActivity.getId(), e.getMessage(), e);
            // 预热失败不影响活动创建，可以后续手动刷新
        }
        
        return savedActivity.getId();
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

        // 如果修改了时间，需要检查是否与其他活动冲突
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            List<SeckillActivity> conflictingActivities = seckillActivityRepository.findByProduct_IdAndStatusIn(
                activity.getProduct().getId(), java.util.Arrays.asList(0, 1));
            
            // 排除当前活动本身
            conflictingActivities.removeIf(a -> a.getId().equals(activityId));
            
            if (!conflictingActivities.isEmpty()) {
                throw new RuntimeException("该商品在其他时间段已有秒杀活动，无法修改时间");
            }
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
     * 抢购秒杀商品（不指定SKU，使用活动级别库存）
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 是否抢购成功
     */
    @Override
    @Transactional
    public boolean seckillProduct(String userId, Long activityId) {
        return seckillProductWithSku(userId, activityId, null);
    }

    /**
     * 抢购秒杀商品（指定SKU）
     * 注意：现在秒杀活动不再指定SKU，用户必须传skuId选择要购买的SKU
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param skuId SKU ID（必须指定）
     * @return 是否抢购成功
     */
    @Override
    public boolean seckillProductWithSku(String userId, Long activityId, Long skuId) {
        // 1. 验证SKU参数（现在必须指定SKU）
        if (skuId == null) {
            log.warn("秒杀必须指定SKU: userId={}, activityId={}", userId, activityId);
            return false;
        }
        
        // 2. 从Redis检查并扣减指定SKU的库存（原子操作）
        boolean stockDecreased = seckillStockUtil.decreaseSkuStock(activityId, skuId);
        log.debug("尝试扣减SKU库存: userId={}, activityId={}, skuId={}", userId, activityId, skuId);

        if (!stockDecreased) {
            log.warn("秒杀失败，SKU库存不足或扣减失败: userId={}, activityId={}, skuId={}", 
                    userId, activityId, skuId);
            return false;
        }

        try {
            // 3. 验证活动有效性
            SeckillActivity activity = seckillActivityRepository.findById(activityId)
                    .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

            if (!activity.isValid()) {
                // 如果活动无效，恢复库存
                seckillStockUtil.restoreSkuStock(activityId, skuId);
                log.warn("秒杀失败，活动无效: userId={}, activityId={}, skuId={}", 
                        userId, activityId, skuId);
                return false;
            }

            // 4. 验证SKU是否属于该活动的商品
            Product product = activity.getProduct();
            if (product == null || product.getSkus() == null) {
                seckillStockUtil.restoreSkuStock(activityId, skuId);
                log.warn("秒杀失败，商品或SKU列表为空: userId={}, activityId={}, skuId={}", 
                        userId, activityId, skuId);
                return false;
            }
            
            boolean skuBelongsToProduct = product.getSkus().stream()
                    .anyMatch(sku -> sku.getId().equals(skuId));
            
            if (!skuBelongsToProduct) {
                seckillStockUtil.restoreSkuStock(activityId, skuId);
                log.warn("秒杀失败，SKU不属于该商品: userId={}, activityId={}, skuId={}, productId={}", 
                        userId, activityId, skuId, product.getId());
                return false;
            }

            // TODO: 检查用户限购

            // 5. 更新数据库中的已售数量（异步或定时同步）
            activity.setSoldCount(activity.getSoldCount() + 1);
            seckillActivityRepository.save(activity);

            log.info("秒杀成功: userId={}, activityId={}, skuId={}", userId, activityId, skuId);
            return true;

        } catch (Exception e) {
            // 发生异常时恢复库存
            seckillStockUtil.restoreSkuStock(activityId, skuId);
            log.error("秒杀异常，已恢复库存: userId={}, activityId={}, skuId={}, error={}", 
                    userId, activityId, skuId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 回滚秒杀库存（用于订单创建失败时）
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     */
    @Override
    public void rollbackSeckillStock(Long activityId, Long skuId) {
        seckillStockUtil.restoreSkuStock(activityId, skuId);
        log.info("回滚秒杀库存: activityId={}, skuId={}", activityId, skuId);
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
        activity.setLimitPerUser(dto.getLimitPerUser());
        activity.setUsedCount(0);
        activity.setSort(dto.getSort() != null ? dto.getSort() : 0);

        // 如果是指定商品的活动，设置商品关联
        if ("product".equals(dto.getScopeType()) && dto.getScopeIds() != null) {
            List<Long> productIds = parseProductIdsFromJson(dto.getScopeIds());
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
                activity.addProduct(product);
            }
        }

        DiscountActivity savedActivity = discountActivityRepository.save(activity);
        
        return savedActivity.getId();
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
        activity.setLimitPerUser(dto.getLimitPerUser());
        activity.setSort(dto.getSort());

        // 如果是指定商品的活动，更新商品关联
        if ("product".equals(dto.getScopeType()) && dto.getScopeIds() != null) {
            // 先清空现有商品关联
            activity.getProducts().clear();
            
            // 添加新的商品关联
            List<Long> productIds = parseProductIdsFromJson(dto.getScopeIds());
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
                activity.addProduct(product);
            }
        }

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
        
        // 将商品ID列表转换为JSON字符串（用于返回给前端）
        if (activity.getProducts() != null && !activity.getProducts().isEmpty()) {
            List<Long> productIds = activity.getProducts().stream()
                .map(Product::getId)
                .collect(java.util.stream.Collectors.toList());
            vo.setScopeIds(productIds.toString());
        }
        
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

    /**
     * 从 JSON 字符串解析商品ID列表
     *
     * @param jsonStr JSON字符串
     * @return 商品ID列表
     */
    private List<Long> parseProductIdsFromJson(String jsonStr) {
        try {
            // 简单解析 JSON 数组，例如: [1,2,3]
            String cleaned = jsonStr.replaceAll("[\\[\\]\\s]", "");
            if (cleaned.isEmpty()) {
                return new ArrayList<>();
            }
            
            return java.util.Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("解析商品ID列表失败 - jsonStr: {}", jsonStr, e);
            return new ArrayList<>();
        }
    }
}
