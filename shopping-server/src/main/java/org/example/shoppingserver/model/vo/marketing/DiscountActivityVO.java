package org.example.shoppingserver.model.vo.marketing;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 满减活动VO
 */
@Data
public class DiscountActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 优惠类型（1-满件减，2-满额减）
     */
    private Integer discountType;

    /**
     * 条件值（满件数或满金额）
     */
    private BigDecimal conditionValue;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 最大优惠金额（封顶）
     */
    private BigDecimal maxDiscount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态（0-未开始，1-进行中，2-已结束，3-已取消）
     */
    private Integer status;

    /**
     * 适用范围类型（1-全场，2-指定商品，3-指定分类）
     */
    private String scopeType;

    /**
     * 适用范围ID列表
     */
    private String scopeIds;

    /**
     * 每人限用次数
     */
    private Integer limitPerUser;

    /**
     * 已使用次数
     */
    private Integer usedCount;

    /**
     * 排序
     */
    private Integer sort;
}
