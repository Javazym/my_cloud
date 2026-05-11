package org.example.shoppingserver.model.dto.coupon;

import lombok.Data;

/**
 * 验证结果DTO
 */
@Data
public class ValidateResultDTO {
    /**
     * 是否有效
     */
    private boolean valid;
    
    /**
     * 消息提示
     */
    private String message;
    
    /**
     * 优惠金额
     */
    private java.math.BigDecimal discountAmount;
}
