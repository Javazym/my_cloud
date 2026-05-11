package org.example.shoppingserver.model.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款VO
 */
@Data
public class RefundVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款类型：0-仅退款，1-退货退款
     */
    private Integer refundType;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款描述
     */
    private String description;

    /**
     * 图片凭证（JSON数组）
     */
    private String images;

    /**
     * 退款状态码
     */
    private Integer status;

    /**
     * 退款状态描述
     */
    private String statusText;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}
