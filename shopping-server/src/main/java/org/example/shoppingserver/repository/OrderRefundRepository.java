package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.order.RefundStatus;
import org.example.shoppingserver.model.entity.order.OrderRefund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 退款/售后Repository
 */
@Repository
public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {

    /**
     * 根据退款单号查询退款记录
     */
    Optional<OrderRefund> findByRefundNo(String refundNo);

    /**
     * 根据订单ID查询退款记录
     */
    List<OrderRefund> findByOrderId(Long orderId);

    /**
     * 根据用户ID查询退款记录
     */
    Page<OrderRefund> findByUserId(String userId, Pageable pageable);

    /**
     * 根据用户ID和状态查询退款记录
     */
    Page<OrderRefund> findByUserIdAndStatus(String userId, RefundStatus status, Pageable pageable);

    /**
     * 根据商家ID查询退款记录
     */
    Page<OrderRefund> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态查询退款记录
     */
    Page<OrderRefund> findByMerchantIdAndStatus(Long merchantId, RefundStatus status, Pageable pageable);

    /**
     * 根据状态查询退款记录
     */
    List<OrderRefund> findByStatus(RefundStatus status);

    /**
     * 统计商家待处理退款数量
     */
    long countByMerchantIdAndStatus(Long merchantId, RefundStatus status);

    /**
     * 统计用户退款数量
     */
    long countByUserId(String userId);

    /**
     * 查询订单的退款记录
     */
    Optional<OrderRefund> findByOrderIdAndStatus(Long orderId, RefundStatus status);

    /**
     * 检查订单是否有指定状态的退款申请
     */
    boolean existsByOrderIdAndStatusIn(Long orderId, List<RefundStatus> statuses);
}
