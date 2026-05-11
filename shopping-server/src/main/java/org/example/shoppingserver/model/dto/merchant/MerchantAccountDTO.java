package org.example.shoppingserver.model.dto.merchant;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家账户DTO
 */
@Data
public class MerchantAccountDTO {
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 可提现金额
     */
    private BigDecimal withdrawable;
    
    /**
     * 已提现金额
     */
    private BigDecimal withdrawn;
    
    /**
     * 待提现金额
     */
    private BigDecimal pendingWithdraw;
}
