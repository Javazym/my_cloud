package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.order.OrderStatus;
import org.example.shoppingserver.model.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * 根据订单号查询订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    Page<Order> findByUserId(String userId, Pageable pageable);

    /**
     * 根据用户ID和状态查询订单列表
     */
    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);

    /**
     * 根据商家ID查询订单列表
     */
    Page<Order> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态查询订单列表
     */
    Page<Order> findByMerchantIdAndStatus(Long merchantId, OrderStatus status, Pageable pageable);

    /**
     * 查询用户待付款订单
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 0")
    List<Order> findPendingPaymentOrders(@Param("userId") String userId);

    /**
     * 查询用户待发货订单
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 1")
    List<Order> findPendingShipmentOrders(@Param("userId") String userId);

    /**
     * 查询用户待收货订单
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 2")
    List<Order> findPendingReceiptOrders(@Param("userId") String userId);

    /**
     * 查询用户待评价订单
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 3")
    List<Order> findPendingReviewOrders(@Param("userId") String userId);

    /**
     * 统计用户订单数量
     */
    long countByUserId(String userId);

    /**
     * 统计商家订单数量
     */
    long countByMerchantId(Long merchantId);

    /**
     * 统计用户各状态订单数量
     */
    long countByUserIdAndStatus(String userId, OrderStatus status);

    /**
     * 统计商家各状态订单数量
     */
    long countByMerchantIdAndStatus(Long merchantId, OrderStatus status);

    /**
     * 统计商家订单总数（使用JPQL）
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.merchant.id = :merchantId")
    long countOrdersByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 统计商家各状态订单数量（使用JPQL）
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.merchant.id = :merchantId AND o.status = :status")
    long countOrdersByMerchantIdAndStatus(@Param("merchantId") Long merchantId, @Param("status") OrderStatus status);

    /**
     * 查询超时未付款订单
     */
    @Query("SELECT o FROM Order o WHERE o.status = 0 AND o.createdAt < :time")
    List<Order> findTimeoutOrders(@Param("time") LocalDateTime time);

    /**
     * 统计今日订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startTime")
    long countTodayOrders(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计今日销售额
     */
    @Query("SELECT SUM(o.payAmount) FROM Order o WHERE o.createdAt >= :startTime AND o.status >= 3")
    java.math.BigDecimal sumTodaySales(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计指定状态订单数量
     */
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
}
