package org.example.shoppingserver.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 物流信息DTO
 */
@Data
public class OrderLogisticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物流ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流单号
     */
    private String trackingNumber;

    /**
     * 当前状态
     */
    private String currentStatus;

    /**
     * 物流轨迹
     */
    private String traces;

    /**
     * 商家 ID
     */
    private Long merchantId;
}
