package org.example.shoppingserver.model.vo.coupon;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券VO
 */
@Data
public class CouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：0-满减，1-折扣
     */
    private Integer type;

    /**
     * 面值/折扣
     */
    private BigDecimal value;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 最高优惠金额
     */
    private BigDecimal maxDiscount;

    /**
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 已领取数量
     */
    private Integer receiveCount;

    /**
     * 已使用数量
     */
    private Integer usedCount;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;

    /**
     * 有效天数（从领取日起）
     */
    private Integer validDays;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;
}
