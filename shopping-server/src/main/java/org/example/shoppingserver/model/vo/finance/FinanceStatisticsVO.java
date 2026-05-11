package org.example.shoppingserver.model.vo.finance;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 财务统计VO
 */
@Data
public class FinanceStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总提现金额（已打款）
     */
    private BigDecimal totalWithdraw;

    /**
     * 待审核提现金额
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
     * 待审核提现次数
     */
    private Long pendingWithdrawCount;
}
