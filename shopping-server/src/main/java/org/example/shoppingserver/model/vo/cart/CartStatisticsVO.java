package org.example.shoppingserver.model.vo.cart;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车统计信息VO
 */
@Data
public class CartStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
