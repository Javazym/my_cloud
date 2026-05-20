package org.example.shoppingserver.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.coupon.CouponQueryDTO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.model.vo.coupon.UserCouponVO;
import org.example.shoppingserver.model.dto.coupon.ValidateResultDTO;
import org.example.shoppingserver.model.entity.coupon.Coupon;
import org.example.shoppingserver.model.entity.coupon.UserCoupon;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.vo.coupon.ValidateResultVO;
import org.example.shoppingserver.repository.CouponRepository;
import org.example.shoppingserver.repository.UserCouponRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.CouponService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    // ====================== 1. 查询优惠券列表 ======================
    @Override
    public List<CouponVO> getCoupons(CouponQueryDTO queryDTO) {
        LocalDateTime now = LocalDateTime.now();
        Specification<Coupon> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            if (queryDTO.getMerchantId() != null) {
                list.add(cb.equal(root.get("merchant").get("id"), queryDTO.getMerchantId()));
            }
            if (queryDTO.getStatus() != null) {
                list.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            
            // 添加有效期校验：只查询在有效期内的优惠券
            list.add(cb.lessThanOrEqualTo(root.get("startTime"), now));
            list.add(cb.greaterThanOrEqualTo(root.get("endTime"), now));

            return cb.and(list.toArray(new Predicate[0]));
        };

        return couponRepository.findAll(spec).stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
    }

    // ====================== 2. 获取可用优惠券 ======================
    @Override
    public List<CouponVO> getAvailableCoupons(String userId, BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findAvailableCoupons(now).stream()
                .filter(coupon -> coupon.isValid()) // 确保优惠券在有效期内
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
    }

    // ====================== 3. 优惠券详情 ======================
    @Override
    @Cacheable(value = "couponDetail", key = "#couponId", unless = "#result == null")
    public CouponVO getCouponDetail(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));
        return convertToVO(coupon);
    }

    // ====================== 4. 领取优惠券（核心） ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"couponDetail", "availableCoupons"}, allEntries = true)
    public boolean receiveCoupon(String userId, Long couponId) {
        // 1. 检查优惠券
        Coupon coupon = couponRepository.findById(couponId).orElse(null);
        if (!coupon.isValid() || !coupon.hasStock()) {
            return false;
        }

        // 2. 检查每人限领
        long count = userCouponRepository.countByUserIdAndCouponId(userId, couponId);
        if (count >= coupon.getLimitPerUser()) {
            return false;
        }

        // 3. 构建用户优惠券
        User user = userRepository.findById(userId).orElseThrow();
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setValue(coupon.getValue());
        userCoupon.setMinAmount(coupon.getMinAmount());
        userCoupon.setMaxDiscount(coupon.getMaxDiscount());
        userCoupon.setStatus(0);
        userCoupon.setReceiveTime(LocalDateTime.now());

        // 4. 计算过期时间
        if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            userCoupon.setExpireTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        } else {
            userCoupon.setExpireTime(coupon.getEndTime());
        }

        // 5. 保存
        userCouponRepository.save(userCoupon);
        couponRepository.incrementReceiveCount(couponId);
        return true;
    }

    // ====================== 5. 查询我的优惠券 ======================
    @Override
    public Page<UserCouponVO> getUserCoupons(String userId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserCoupon> page;

        if (status == null) {
            page = userCouponRepository.findByUserId(userId, pageable);
        } else {
            page = userCouponRepository.findByUserIdAndStatus(userId, status, pageable);
        }

        return page.map(this::convertToUserCouponVO);
    }

    // ====================== 6. 验证优惠券 ======================
    @Override
    public ValidateResultVO validateCoupon(String userId, Long couponId, BigDecimal orderAmount) {
        ValidateResultVO dto = new ValidateResultVO();
        Optional<UserCoupon> optional = userCouponRepository.findByUserIdAndCouponId(userId, couponId);

        if (optional.isEmpty()) {
            dto.setValid(false);
            dto.setMessage("未领取该优惠券");
            return dto;
        }

        UserCoupon userCoupon = optional.get();
        if (!userCoupon.isAvailable()) {
            dto.setValid(false);
            dto.setMessage("优惠券不可用");
            return dto;
        }

        BigDecimal discount = userCoupon.calculateDiscount(orderAmount);
        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            dto.setValid(false);
            dto.setMessage("订单金额未达到使用门槛");
            return dto;
        }

        dto.setValid(true);
        dto.setMessage("校验通过");
        dto.setDiscountAmount(discount);
        return dto;
    }

    // ====================== 7. 使用优惠券 ======================
    @Override
    @Transactional
    @CacheEvict(value = {"userCoupons", "availableCoupons"}, allEntries = true)
    public boolean useCoupon(String userId, Long couponId, Long orderId) {
        Optional<UserCoupon> optional = userCouponRepository.findByUserIdAndCouponId(userId, couponId);
        if (optional.isEmpty()) return false;

        UserCoupon uc = optional.get();
        if (uc.getStatus() != 0) return false;

        userCouponRepository.useCoupon(uc.getId(), orderId, LocalDateTime.now());
        couponRepository.incrementUsedCount(couponId);
        return true;
    }

    // ====================== 8. 清理过期优惠券 ======================
    @Override
    @Transactional
    @CacheEvict(value = {"userCoupons", "availableCoupons"}, allEntries = true)
    public int cleanExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        int count = userCouponRepository.updateExpiredStatus(now);
        
        if (count > 0) {
            log.info("清理过期优惠券完成，共更新 {} 张优惠券状态为已过期", count);
        }
        
        return count;
    }

    // ====================== 9. 获取指定商品的可用优惠券 ======================
    @Override
    public List<CouponVO> getAvailableCouponsForProduct(Long productId, Long merchantId) {
        String userId = UserHolder.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 获取适用于该商品的可用优惠券
        List<Coupon> availableCoupons = couponRepository.findAvailableCouponsByProductId(
                String.valueOf(productId), 
                merchantId, 
                now);
        
        // 2. 获取用户已领取的优惠券ID列表
        List<UserCoupon> userCoupons = userCouponRepository.findAllByUserId(userId);
        Set<Long> receivedCouponIds = userCoupons.stream()
                .map(uc -> uc.getCoupon().getId())
                .collect(java.util.stream.Collectors.toSet());
        
        // 3. 过滤掉用户已领取的优惠券
        return availableCoupons.stream()
                .filter(coupon -> coupon.isValid()) // 确保优惠券在有效期内
                .filter(coupon -> !receivedCouponIds.contains(coupon.getId())) // 过滤已领取的
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
    }

    // ====================== 10. 获取用户可用于指定商品的优惠券 ======================
    @Override
    public List<UserCouponVO> getUserAvailableCouponsForProduct(String userId, Long productId, Long merchantId) {
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> userCoupons = userCouponRepository.findAvailableCouponsWithDetail(userId, now);
        
        // 过滤出适用于该商品的优惠券
        return userCoupons.stream()
                .filter(userCoupon -> isCouponApplicableForProduct(userCoupon.getCoupon(), productId, merchantId))
                .map(this::convertToUserCouponVO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 判断优惠券是否适用于指定商品
     */
    private boolean isCouponApplicableForProduct(Coupon coupon, Long productId, Long merchantId) {
        if (coupon == null || !coupon.isValid()) {
            return false;
        }
        
        String scope = coupon.getScope();
        
        // 全场券：所有商品都可用
        if ("all".equals(scope)) {
            return true;
        }
        
        // 商家券：检查是否是同一商家
        if (coupon.getMerchant() != null && coupon.getMerchant().getId().equals(merchantId)) {
            return true;
        }
        
        // 商品券：检查是否包含该商品
        if ("product".equals(scope) && coupon.getProductIds() != null) {
            return coupon.getProductIds().contains(String.valueOf(productId));
        }
        
        // 分类券：需要检查商品分类（这里简化处理，暂不支持）
        // 如果需要支持分类券，需要查询商品的分类ID并匹配
        
        return false;
    }

    // ====================== 工具：转VO ======================
    private UserCouponVO convertToUserCouponVO(UserCoupon uc) {
        UserCouponVO vo = new UserCouponVO();
        vo.setId(uc.getId());
        vo.setCouponId(uc.getCoupon().getId());
        vo.setCouponName(uc.getCouponName());
        vo.setCouponType(uc.getCouponType());
        vo.setValue(uc.getValue());
        vo.setMinAmount(uc.getMinAmount());
        vo.setMaxDiscount(uc.getMaxDiscount());
        vo.setStatus(uc.getStatus());
        vo.setExpireTime(uc.getExpireTime());
        return vo;
    }

    private CouponVO convertToVO(Coupon coupon) {
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