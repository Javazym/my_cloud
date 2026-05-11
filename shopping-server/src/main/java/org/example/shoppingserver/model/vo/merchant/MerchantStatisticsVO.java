package org.example.shoppingserver.model.vo.merchant;

import lombok.Data;

import java.io.Serializable;

/**
 * 商家统计VO
 */
@Data
public class MerchantStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品总数
     */
    private Integer productCount;

    /**
     * 订单总数
     */
    private Integer orderCount;

    /**
     * 今日订单数
     */
    private Integer todayOrderCount;

    /**
     * 今日销售额
     */
    private java.math.BigDecimal todaySales;

    /**
     * 本月销售额
     */
    private java.math.BigDecimal monthSales;

    /**
     * 总销售额
     */
    private java.math.BigDecimal totalSales;

    /**
     * 粉丝数
     */
    private Integer followers;

    /**
     * 评分
     */
    private java.math.BigDecimal rating;
}
