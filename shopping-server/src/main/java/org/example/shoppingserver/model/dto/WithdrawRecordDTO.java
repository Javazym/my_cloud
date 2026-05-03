package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录DTO
 */
@Data
public class WithdrawRecordDTO {
    /**
     * 提现ID
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;

    /**
     * 收款账户/账户姓名
     */
    private String account;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 状态：0-待审核，1-已通过，2-已驳回，3-已打款
     */
    private Integer status;

    /**
     * 审核原因
     */
    private String reason;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 打款时间
     */
    private LocalDateTime transferTime;
}
