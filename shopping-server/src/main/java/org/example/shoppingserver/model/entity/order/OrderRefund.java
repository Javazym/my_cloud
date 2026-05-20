package org.example.shoppingserver.model.entity.order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.merchant.Merchant;

import java.math.BigDecimal;

/**
 * 退款/售后实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_refunds")
public class OrderRefund extends BaseEntity {

    /**
     * 退款单号
     */
    @Column(name = "refund_no", nullable = false, unique = true, length = 64)
    private String refundNo;

    /**
     * 订单ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 订单商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 商家ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 类型：0-退款，1-退货退款
     */
    @Column(name = "type")
    private Integer type = 0;

    /**
     * 退款金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 退款原因
     */
    @Column(name = "reason", length = 200)
    private String reason;

    /**
     * 退款说明
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 图片凭证（JSON数组）
     */
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private RefundStatus status = RefundStatus.APPLYING;

    /**
     * 拒绝原因
     */
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;
}
