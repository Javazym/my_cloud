package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物流信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_logistics")
public class OrderLogistics extends BaseEntity {

    /**
     * 订单ID
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 物流公司
     */
    @Column(name = "logistics_company", length = 50)
    private String logisticsCompany;

    /**
     * 物流单号
     */
    @Column(name = "tracking_number", length = 64)
    private String trackingNumber;

    /**
     * 当前状态
     */
    @Column(name = "current_status", length = 50)
    private String currentStatus;

    /**
     * 物流轨迹（JSON）
     */
    @Column(name = "traces", columnDefinition = "JSON")
    private String traces;
}
