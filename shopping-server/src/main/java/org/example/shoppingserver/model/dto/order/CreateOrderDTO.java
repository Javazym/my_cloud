package org.example.shoppingserver.model.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class CreateOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    private Long addressId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 购物车商品ID列表
     */
    private List<Long> cartItemIds;

    /**
     * 商品列表（直接购买）
     */
    private List<OrderItemCreateDTO> items;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 发票信息
     */
    private InvoiceCreateDTO invoice;

    /**
     * 订单类型
     */
    private int status;

    /**
     * 订单商品创建DTO
     */
    @Data
    public static class OrderItemCreateDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long productId;
        private Long skuId;
        private Integer quantity;
    }

    /**
     * 发票创建DTO
     */
    @Data
    public static class InvoiceCreateDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer type;
        private String title;
        private String taxNumber;
    }
}
