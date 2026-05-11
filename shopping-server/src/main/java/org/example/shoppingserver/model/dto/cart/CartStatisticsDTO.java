package org.example.shoppingserver.model.dto.cart;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车统计信息DTO
 */
@Data
public class CartStatisticsDTO {
    /**
     * 总商品数量（不同SKU数）
     */
    private Integer totalCount;
    
    /**
     * 选中商品数量
     */
    private Integer selectedCount;
    
    /**
     * 总金额（所有商品）
     */
    private BigDecimal totalAmount;
    
    /**
     * 选中金额
     */
    private BigDecimal selectedAmount;
}
