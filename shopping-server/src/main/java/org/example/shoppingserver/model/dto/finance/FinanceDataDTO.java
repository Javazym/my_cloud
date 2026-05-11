package org.example.shoppingserver.model.dto.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务数据DTO
 */
@Data
public class FinanceDataDTO {
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 本月收入
     */
    private BigDecimal monthIncome;
    
    /**
     * 今日收入
     */
    private BigDecimal todayIncome;
    
    /**
     * 订单数量
     */
    private Long orderCount;
    
    /**
     * 本月订单数
     */
    private Long monthOrderCount;
    
    /**
     * 平均订单金额
     */
    private BigDecimal averageOrderAmount;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
}
