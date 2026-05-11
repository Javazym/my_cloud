package org.example.shoppingserver.model.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单统计VO
 */
@Data
public class OrderStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 待付款
     */
    private Long pendingPayment;

    /**
     * 待发货
     */
    private Long pendingShipment;

    /**
     * 待收货
     */
    private Long pendingReceipt;

    /**
     * 已完成
     */
    private Long completed;

    /**
     * 已取消
     */
    private Long cancelled;

    /**
     * 今日销售额
     */
    private BigDecimal todaySales;

    /**
     * 本月销售额
     */
    private BigDecimal monthSales;
}
