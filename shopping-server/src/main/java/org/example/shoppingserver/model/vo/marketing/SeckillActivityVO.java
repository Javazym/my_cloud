package org.example.shoppingserver.model.vo.marketing;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动VO
 */
@Data
public class SeckillActivityVO implements Serializable {

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
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 每人限购
     */
    private Integer limitPerUser;

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
     * 排序
     */
    private Integer sort;

    // ==================== 商品基本信息 ====================

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品副标题
     */
    private String productSubName;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * 商品价格（原价）
     */
    private BigDecimal productPrice;

    /**
     * 商品库存
     */
    private Integer productStock;

    /**
     * 商品销量
     */
    private Integer productSoldCount;

    /**
     * 商品评分
     */
    private BigDecimal productRating;
}
