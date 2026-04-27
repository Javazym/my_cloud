package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDTO implements Serializable {

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
     * 商家名称
     */
    private String merchantName;

    /**
     * 订单总额
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
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 消耗积分
     */
    private Integer points;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态文本
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
     * 收货人
     */
    private String receiverName;

    /**
     * 收货电话
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
     * 订单商品列表
     */
    private List<OrderItemDTO> items;

    /**
     * 物流信息
     */
    private OrderLogisticsDTO logistics;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 退款信息VO
     */
    @Data
    public static class RefundVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;                  // 退款ID
        private String orderNo;           // 订单号
        private Long orderId;             // 订单ID
        private String userId;            // 用户ID
        private String userName;          // 用户名
        private BigDecimal refundAmount;  // 退款金额
        private Integer refundType;       // 退款类型：1-仅退款，2-退货退款
        private String reason;            // 退款原因
        private String description;       // 退款说明
        private String images;            // 凭证图片(JSON)
        private Integer status;           // 退款状态：0-待审核，1-已同意，2-已拒绝，3-退款中，4-已退款
        private String statusText;        // 退款状态文本
        private LocalDateTime applyTime;  // 申请时间
        private LocalDateTime handleTime; // 处理时间
        private String rejectReason;      // 拒绝原因
    }
}
