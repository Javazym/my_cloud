package org.example.shoppingserver.model.entity.order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.model.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    /**
     * 订单号
     */
    @Column(name = "order_no", nullable = false, unique = true, length = 64)
    private String orderNo;

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
     * 订单总额
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 优惠金额
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 优惠券金额
     */
    @Column(name = "coupon_amount", precision = 10, scale = 2)
    private BigDecimal couponAmount = BigDecimal.ZERO;

    /**
     * 运费
     */
    @Column(name = "freight_amount", precision = 10, scale = 2)
    private BigDecimal freightAmount = BigDecimal.ZERO;

    /**
     * 实付金额
     */
    @Column(name = "pay_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 消耗积分
     */
    @Column(name = "points")
    private Integer points = 0;

    /**
     * 订单状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 0")
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    @Column(name = "ship_time")
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    /**
     * 完成时间
     */
    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    /**
     * 收货人
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货电话
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * 收货地址
     */
    @Column(name = "receiver_address", nullable = false, length = 255)
    private String receiverAddress;

    /**
     * 订单备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 订单商品列表
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * 物流信息
     */
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderLogistics logistics;

    /**
     * 添加订单商品
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * 计算订单总额
     */
    public void recalculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice());
        }
        this.totalAmount = total;
        this.payAmount = total.subtract(discountAmount).subtract(couponAmount).add(freightAmount);
    }

    /**
     * 支付订单
     */
    public void pay() {
        this.status = OrderStatus.PENDING_SHIPMENT;
        this.payTime = LocalDateTime.now();
    }

    /**
     * 发货
     */
    public void ship() {
        this.status = OrderStatus.PENDING_RECEIPT;
        this.shipTime = LocalDateTime.now();
    }

    /**
     * 确认收货
     */
    public void confirmReceipt() {
        this.status = OrderStatus.COMPLETED;
        this.receiveTime = LocalDateTime.now();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
