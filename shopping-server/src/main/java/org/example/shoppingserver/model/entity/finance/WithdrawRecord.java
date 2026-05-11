package org.example.shoppingserver.model.entity.finance;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.merchant.Merchant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "withdraw_records")
public class WithdrawRecord extends BaseEntity {

    /**
     * 商家ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 提现金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 手续费
     */
    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * 实际到账
     */
    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount;

    /**
     * 银行名称
     */
    @Column(name = "bank_name", length = 50)
    private String bankName;

    /**
     * 银行账号
     */
    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    /**
     * 账户姓名
     */
    @Column(name = "account_name", length = 50)
    private String accountName;

    /**
     * 状态：0-待审核，1-审核通过，2-已打款，3-拒绝
     */
    @Column(name = "status")
    private Integer status = 0;

    /**
     * 审核备注/拒绝原因
     */
    @Column(name = "audit_reason", length = 255)
    private String auditReason;

    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 打款时间
     */
    @Column(name = "transfer_time")
    private LocalDateTime transferTime;

    /**
     * 处理时间
     */
    @Column(name = "process_time")
    private LocalDateTime processTime;

    /**
     * 审核通过
     */
    public void approve() {
        this.status = 1;
        this.auditTime = LocalDateTime.now();
        this.processTime = LocalDateTime.now();
    }

    /**
     * 打款完成
     */
    public void completed() {
        this.status = 2;
        this.transferTime = LocalDateTime.now();
        this.processTime = LocalDateTime.now();
    }

    /**
     * 拒绝
     */
    public void reject(String reason) {
        this.status = 3;
        this.auditReason = reason;
        this.auditTime = LocalDateTime.now();
        this.processTime = LocalDateTime.now();
    }
}
