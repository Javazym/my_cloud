package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    /**
     * 商家ID：0-平台券
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    /**
     * 优惠券名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 类型：0-满减，1-折扣
     */
    @Column(name = "type")
    private Integer type = 0;

    /**
     * 面值/折扣
     */
    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    /**
     * 最低消费
     */
    @Column(name = "min_amount", precision = 10, scale = 2)
    private BigDecimal minAmount = BigDecimal.ZERO;

    /**
     * 最高优惠
     */
    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    /**
     * 发放总量
     */
    @Column(name = "total_count")
    private Integer totalCount = 0;

    /**
     * 已领取数量
     */
    @Column(name = "receive_count")
    private Integer receiveCount = 0;

    /**
     * 已使用数量
     */
    @Column(name = "used_count")
    private Integer usedCount = 0;

    /**
     * 每人限领
     */
    @Column(name = "limit_per_user")
    private Integer limitPerUser = 1;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 领取后天数
     */
    @Column(name = "valid_days")
    private Integer validDays;

    /**
     * 使用范围：all-全场，category-分类，product-商品
     */
    @Column(name = "scope", length = 20)
    private String scope = "all";

    /**
     * 分类ID（JSON数组）
     */
    @Column(name = "category_ids", length = 500)
    private String categoryIds;

    /**
     * 商品ID（JSON数组）
     */
    @Column(name = "product_ids", length = 500)
    private String productIds;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 是否在有效期内
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 && now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * 是否还有库存
     */
    public boolean hasStock() {
        return totalCount == 0 || receiveCount < totalCount;
    }
}
