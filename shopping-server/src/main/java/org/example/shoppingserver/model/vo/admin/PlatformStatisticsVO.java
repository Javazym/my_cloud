package org.example.shoppingserver.model.vo.admin;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 平台统计数据VO
 */
@Data
public class PlatformStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 今日新增用户数
     */
    private Long todayUsers;

    /**
     * 总商家数
     */
    private Long totalMerchants;

    /**
     * 今日新增商家数
     */
    private Long todayMerchants;

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 今日订单数
     */
    private Long todayOrders;

    /**
     * 总销售额
     */
    private BigDecimal totalSales;

    /**
     * 今日销售额
     */
    private BigDecimal todaySales;

    /**
     * 总商品数
     */
    private Long totalProducts;

    /**
     * 待处理退款数
     */
    private Long pendingRefunds;

    /**
     * 待审核商家数
     */
    private Long pendingAudits;
}
