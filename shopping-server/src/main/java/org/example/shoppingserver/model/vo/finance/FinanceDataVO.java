package org.example.shoppingserver.model.vo.finance;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 财务数据VO
 */
@Data
public class FinanceDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总收入
     */
    private BigDecimal totalIncome;

    /**
     * 总支出
     */
    private BigDecimal totalExpense;

    /**
     * 净利润
     */
    private BigDecimal netProfit;

    /**
     * 订单数量
     */
    private Long orderCount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 每日收入统计 (日期 -> 金额)
     */
    private Map<String, BigDecimal> dailyIncome;
}
