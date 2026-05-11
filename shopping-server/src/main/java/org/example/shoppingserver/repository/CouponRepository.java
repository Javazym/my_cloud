package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.coupon.Coupon;
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

/**
 * 优惠券Repository
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {

    /**
     * 根据商家ID查询优惠券列表
     */
    List<Coupon> findByMerchantId(Long merchantId);

    /**
     * 根据商家ID分页查询优惠券
     */
    Page<Coupon> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态分页查询优惠券
     */
    Page<Coupon> findByMerchantIdAndStatus(Long merchantId, Integer status, Pageable pageable);

    /**
     * 查询可领取的优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 1 AND c.startTime <= :now AND c.endTime >= :now AND (c.totalCount = 0 OR c.receiveCount < c.totalCount)")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    /**
     * 查询商家的可用优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant.id = :merchantId AND c.status = 1 AND c.startTime <= :now AND c.endTime >= :now")
    List<Coupon> findAvailableByMerchantId(@Param("merchantId") Long merchantId, @Param("now") LocalDateTime now);

    /**
     * 查询平台优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant IS NULL AND c.status = 1")
    List<Coupon> findPlatformCoupons();

    /**
     * 根据状态查询优惠券
     */
    Page<Coupon> findByStatus(Integer status, Pageable pageable);

    /**
     * 增加领取数量
     */
    @Modifying
    @Query("UPDATE Coupon c SET c.receiveCount = c.receiveCount + 1 WHERE c.id = :id")
    void incrementReceiveCount(@Param("id") Long id);

    /**
     * 增加使用数量
     */
    @Modifying
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :id")
    void incrementUsedCount(@Param("id") Long id);

    /**
     * 查询已过期的优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.endTime < :now AND c.status = 1")
    List<Coupon> findExpiredCoupons(@Param("now") LocalDateTime now);

    /**
     * 统计商家优惠券数量
     */
    long countByMerchantId(Long merchantId);

    /**
     * 统计商家指定状态的优惠券数量
     */
    long countByMerchantIdAndStatus(Long merchantId, Integer status);

    /**
     * 查询适用于指定商品的可用优惠券
     * 包括：全场券、该商家券、包含该商品的券
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 1 AND c.startTime <= :now AND c.endTime >= :now " +
           "AND (c.totalCount = 0 OR c.receiveCount < c.totalCount) " +
           "AND (c.scope = 'all' OR c.merchant.id = :merchantId OR c.productIds LIKE %:productId%)")
    List<Coupon> findAvailableCouponsByProductId(@Param("productId") String productId, 
                                                  @Param("merchantId") Long merchantId,
                                                  @Param("now") LocalDateTime now);
}
