package org.example.shoppingserver.model.dto.merchant;

import lombok.Data;

/**
 * 商家统计数据DTO
 */
@Data
public class MerchantStatisticsDTO {
    /**
     * 今日销售额
     */
    private Long todaySales;
    
    /**
     * 今日订单数
     */
    private Integer todayOrders;
    
    /**
     * 今日访客数
     */
    private Integer todayVisitors;
    
    /**
     * 待发货订单数
     */
    private Integer pendingOrders;
    
    /**
     * 待收货订单数
     */
    private Integer shippingOrders;
    
    /**
     * 商品总数
     */
    private Integer totalProducts;
    
    /**
     * 总收入
     */
    private Long totalIncome;
}
