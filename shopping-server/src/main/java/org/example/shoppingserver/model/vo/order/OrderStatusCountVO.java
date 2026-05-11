package org.example.shoppingserver.model.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单状态统计VO
 */
@Data
public class OrderStatusCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 待付款订单数
     */
    private Long pendingPayment;

    /**
     * 待发货订单数
     */
    private Long pendingShipment;

    /**
     * 待收货订单数
     */
    private Long pendingReceipt;

    /**
     * 已完成订单数
     */
    private Long completed;

    /**
     * 已取消订单数
     */
    private Long cancelled;

    /**
     * 已退款订单数
     */
    private Long refunded;
}
