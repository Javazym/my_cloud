package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.coupon.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户优惠券Repository
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, JpaSpecificationExecutor<UserCoupon> {

    /**
     * 根据用户ID查询优惠券列表
     */
    Page<UserCoupon> findByUserId(String userId, Pageable pageable);

    /**
     * 根据用户ID查询所有优惠券列表（不分页）
     */
    List<UserCoupon> findAllByUserId(String userId);

    /**
     * 根据用户ID和状态查询优惠券列表
     */
    Page<UserCoupon> findByUserIdAndStatus(String userId, Integer status, Pageable pageable);

    /**
     * 查询用户可用的优惠券
     */
    @Query("SELECT u FROM UserCoupon u WHERE u.user.id = :userId AND u.status = 0 AND u.expireTime > :now")
    List<UserCoupon> findAvailableCoupons(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * 根据用户ID和优惠券ID查询用户优惠券
     */
    Optional<UserCoupon> findByUserIdAndCouponId(String userId, Long couponId);

    /**
     * 查询订单使用的优惠券
     */
    Optional<UserCoupon> findByOrderId(Long orderId);

    /**
     * 查询用户已领取的优惠券数量
     */
    long countByUserIdAndCouponId(String userId, Long couponId);

    /**
     * 查询用户已使用的优惠券数量
     */
    long countByUserIdAndStatus(String userId, Integer status);

    /**
     * 查询过期的优惠券
     */
    @Query("SELECT u FROM UserCoupon u WHERE u.status = 0 AND u.expireTime < :now")
    List<UserCoupon> findExpiredCoupons(@Param("now") LocalDateTime now);

    /**
     * 批量更新过期优惠券状态
     */
    @Modifying
    @Query("UPDATE UserCoupon u SET u.status = 2 WHERE (u.status = 0 OR u.status = 1) AND u.expireTime < :now")
    int updateExpiredStatus(@Param("now") LocalDateTime now);

    /**
     * 使用优惠券
     */
    @Modifying
    @Query("UPDATE UserCoupon u SET u.status = 1, u.useTime = :useTime, u.order.id = :orderId WHERE u.id = :id")
    void useCoupon(@Param("id") Long id, @Param("orderId") Long orderId, @Param("useTime") LocalDateTime useTime);

    /**
     * 统计商家优惠券的总领取数
     */
    @Query("SELECT COUNT(uc) FROM UserCoupon uc WHERE uc.coupon.merchant.id = :merchantId")
    long countByCouponMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 统计商家优惠券的总使用数
     */
    @Query("SELECT COUNT(uc) FROM UserCoupon uc WHERE uc.coupon.merchant.id = :merchantId AND uc.status = :status")
    long countByCouponMerchantIdAndStatus(@Param("merchantId") Long merchantId, @Param("status") Integer status);

    /**
     * 查询用户可用的优惠券（包括优惠券详情）
     */
    @Query("SELECT u FROM UserCoupon u LEFT JOIN FETCH u.coupon WHERE u.user.id = :userId AND u.status = 0 AND u.expireTime > :now")
    List<UserCoupon> findAvailableCouponsWithDetail(@Param("userId") String userId, @Param("now") LocalDateTime now);
}
