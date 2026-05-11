package org.example.shoppingserver.model.vo.order;

import lombok.Data;
import org.example.shoppingserver.model.dto.order.OrderLogisticsDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;

    /**
     * 运费金额
     */
    private BigDecimal freightAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 订单状态码
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusText;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> items;

    /**
     * 物流信息
     */
    private OrderLogisticsDTO logistics;

    /**
     * 状态描述
     */
    private String statusDescription;
}
