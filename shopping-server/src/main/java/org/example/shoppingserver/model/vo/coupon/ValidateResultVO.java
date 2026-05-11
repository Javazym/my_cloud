package org.example.shoppingserver.model.vo.coupon;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券验证结果VO
 */
@Data
public class ValidateResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 优惠券信息
     */
    private String message;
}
