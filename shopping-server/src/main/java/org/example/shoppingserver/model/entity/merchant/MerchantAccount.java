package org.example.shoppingserver.model.entity.merchant;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.math.BigDecimal;

/**
 * 商家账户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "merchant_accounts")
public class MerchantAccount extends BaseEntity {

    /**
     * 商家ID
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;

    /**
     * 总收入
     */
    @Column(name = "total_income", precision = 12, scale = 2)
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 可提现
     */
    @Column(name = "withdrawable", precision = 12, scale = 2)
    private BigDecimal withdrawable = BigDecimal.ZERO;

    /**
     * 已提现
     */
    @Column(name = "withdrawn", precision = 12, scale = 2)
    private BigDecimal withdrawn = BigDecimal.ZERO;

    /**
     * 待提现
     */
    @Column(name = "pending_withdraw", precision = 12, scale = 2)
    private BigDecimal pendingWithdraw = BigDecimal.ZERO;

    /**
     * 增加收入
     */
    public void addIncome(BigDecimal amount) {
        this.totalIncome = this.totalIncome.add(amount);
        this.withdrawable = this.withdrawable.add(amount);
    }

    /**
     * 扣减可提现金额
     */
    public void deductWithdrawable(BigDecimal amount) {
        if (this.withdrawable.compareTo(amount) >= 0) {
            this.withdrawable = this.withdrawable.subtract(amount);
        }
    }

    /**
     * 增加已提现
     */
    public void addWithdrawn(BigDecimal amount) {
        this.withdrawn = this.withdrawn.add(amount);
    }

    /**
     * 增加待提现
     */
    public void addPendingWithdraw(BigDecimal amount) {
        this.pendingWithdraw = this.pendingWithdraw.add(amount);
    }

    /**
     * 减少待提现（审核通过）
     */
    public void reducePendingWithdraw(BigDecimal amount) {
        this.pendingWithdraw = this.pendingWithdraw.subtract(amount);
    }
}
