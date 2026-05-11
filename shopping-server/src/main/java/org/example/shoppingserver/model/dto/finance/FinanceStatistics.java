package org.example.shoppingserver.model.dto.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计数据DTO
 */
@Data
public class FinanceStatistics {
    /**
     * 总余额
     */
    private BigDecimal totalBalance;
    
    /**
     * 总提现金额
     */
    private BigDecimal totalWithdraw;
    
    /**
     * 待提现金额
     */
    private BigDecimal pendingWithdraw;
    
    /**
     * 已完成提现金额
     */
    private BigDecimal completedWithdraw;
    
    /**
     * 总提现次数
     */
    private Long totalWithdrawCount;
    
    /**
     * 待处理提现次数
     */
    private Long pendingWithdrawCount;
}
