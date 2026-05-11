package org.example.shoppingserver.model.entity.marketing;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 满减活动实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "discount_activities")
public class DiscountActivity extends BaseEntity {

    /**
     * 商家ID：0-平台活动
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 活动名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 活动描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 满减类型：1-满件减，2-满额减
     */
    @Column(name = "discount_type", nullable = false)
    private Integer discountType;

    /**
     * 满足条件（件数或金额）
     */
    @Column(name = "condition_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal conditionValue;

    /**
     * 优惠金额
     */
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    /**
     * 最大优惠金额（封顶）
     */
    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;

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
     * 状态：0-未开始，1-进行中，2-已结束，3-已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 适用范围：all-全场商品，category-指定分类，product-指定商品
     */
    @Column(name = "scope_type", length = 20)
    private String scopeType = "all";

    /**
     * 适用范围ID列表（JSON格式）
     */
    @Column(name = "scope_ids", columnDefinition = "TEXT")
    private String scopeIds;

    /**
     * 每人限用次数
     */
    @Column(name = "limit_per_user")
    private Integer limitPerUser;

    /**
     * 总使用次数
     */
    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * 是否有效
     */
    public boolean isValid() {
        return status == 1 && LocalDateTime.now().isAfter(startTime) 
                && LocalDateTime.now().isBefore(endTime);
    }

    /**
     * 更新状态
     */
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            status = 0;
        } else if (now.isAfter(endTime)) {
            status = 2;
        } else {
            status = 1;
        }
    }
}
