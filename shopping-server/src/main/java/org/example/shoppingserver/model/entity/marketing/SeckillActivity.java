package org.example.shoppingserver.model.entity.marketing;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.merchant.Merchant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "seckill_activities")
public class SeckillActivity extends BaseEntity {

    /**
     * 商家ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 商品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * SKU ID
     */
    @Column(name = "sku_id")
    private Long skuId;

    /**
     * 活动名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 秒杀价格
     */
    @Column(name = "seckill_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal seckillPrice;

    /**
     * 原价
     */
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /**
     * 已抢购数量
     */
    @Column(name = "sold_count", nullable = false)
    private Integer soldCount = 0;

    /**
     * 每人限购数量
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
     * 状态：0-未开始，1-进行中，2-已结束，3-已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

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
                && LocalDateTime.now().isBefore(endTime) && stock > soldCount;
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
        } else if (stock <= soldCount) {
            status = 2;
        } else {
            status = 1;
        }
    }
}
