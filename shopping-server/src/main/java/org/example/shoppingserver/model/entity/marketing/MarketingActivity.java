package org.example.shoppingserver.model.entity.marketing;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 营销活动实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "marketing_activities")
public class MarketingActivity extends BaseEntity {

    /**
     * 商家ID：0-平台活动
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    /**
     * 活动名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 活动类型：seckill-秒杀，group-拼团，discount-折扣
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 活动描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
     * 状态：0-未开始，1-进行中，2-已结束
     */
    @Column(name = "status")
    private Integer status = 0;

    /**
     * 是否在活动期间
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * 更新活动状态
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
