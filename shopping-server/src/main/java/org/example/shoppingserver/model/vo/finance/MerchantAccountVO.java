package org.example.shoppingserver.model.vo.finance;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家账户VO
 */
@Data
public class MerchantAccountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 累计收入
     */
    private BigDecimal totalIncome;

    /**
     * 累计提现
     */
    private BigDecimal totalWithdraw;
}
