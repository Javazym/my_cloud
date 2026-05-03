package org.example.shoppingserver.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.entity.Coupon;
import org.example.shoppingserver.model.entity.UserCoupon;
import org.example.shoppingserver.model.entity.User;
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

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    // ====================== 1. 分页查询优惠券 ======================
    @Override
    public Page<Coupon> getCoupons(CouponQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
                queryDTO.getPageNum() - 1,
                queryDTO.getPageSize()
        );

        Specification<Coupon> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            if (queryDTO.getMerchantId() != null) {
                list.add(cb.equal(root.get("merchant").get("id"), queryDTO.getMerchantId()));
            }
            if (queryDTO.getStatus() != null) {
                list.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            return cb.and(list.toArray(new Predicate[0]));
        };

        return couponRepository.findAll(spec, pageable);
    }

    // ====================== 2. 获取可用优惠券 ======================
    @Override
    public List<Coupon> getAvailableCoupons(String userId, BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findAvailableCoupons(now);
    }

    // ====================== 3. 优惠券详情 ======================
    @Override
    @Cacheable(value = "couponDetail", key = "#couponId", unless = "#result == null")
    public Coupon getCouponDetail(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));
    }

    // ====================== 4. 领取优惠券（核心） ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"couponDetail", "availableCoupons"}, allEntries = true)
    public boolean receiveCoupon(String userId, Long couponId) {
        // 1. 检查优惠券
        Coupon coupon = getCouponDetail(couponId);
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
    public Page<UserCouponDTO> getUserCoupons(String userId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserCoupon> page;

        if (status == null) {
            page = userCouponRepository.findByUserId(userId, pageable);
        } else {
            page = userCouponRepository.findByUserIdAndStatus(userId, status, pageable);
        }

        return page.map(this::convertToDTO);
    }

    // ====================== 6. 验证优惠券 ======================
    @Override
    public ValidateResultDTO validateCoupon(String userId, Long couponId, BigDecimal orderAmount) {
        ValidateResultDTO dto = new ValidateResultDTO();
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

    // ====================== 工具：转DTO ======================
    private UserCouponDTO convertToDTO(UserCoupon uc) {
        UserCouponDTO dto = new UserCouponDTO();
        dto.setId(uc.getId());
        dto.setCouponId(uc.getCoupon().getId());
        dto.setCouponName(uc.getCouponName());
        dto.setCouponType(uc.getCouponType());
        dto.setValue(uc.getValue());
        dto.setMinAmount(uc.getMinAmount());
        dto.setMaxDiscount(uc.getMaxDiscount());
        dto.setStatus(uc.getStatus());
        dto.setExpireTime(uc.getExpireTime());
        return dto;
    }
}