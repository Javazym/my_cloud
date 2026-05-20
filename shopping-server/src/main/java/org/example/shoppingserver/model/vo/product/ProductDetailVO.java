package org.example.shoppingserver.model.vo.product;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情VO - 用于商品详情页展示
 */
@Data
public class ProductDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private MerchantInfoVO merchant;
    private CategoryInfoVO category;
    private String name;
    private String subName;
    private String image;
    private List<String> images;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer soldCount;
    private Integer reviewCount;
    private Integer favoriteCount;
    private BigDecimal rating;
    private String description;
    private String detail;
    private Integer isHot;
    private Integer isFeatured;
    private Integer isNew;
    private Integer publishStatus;
    private List<String> tags;
    private String keywords;
    private List<ProductSpecVO> specs;
    private List<ProductSkuVO> skus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 活动信息 ====================

    /**
     * 是否有活动
     */
    private Boolean hasActivity = false;

    /**
     * 活动类型：1-秒杀，2-满减
     */
    private Integer activityType;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动描述（满减活动）
     */
    private String activityDescription;

    /**
     * 活动价格（秒杀价）
     */
    private BigDecimal activityPrice;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityStartTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 活动状态：0-未开始，1-进行中，2-已结束
     */
    private Integer activityStatus;

    /**
     * 秒杀库存（仅秒杀活动）
     */
    private Integer seckillStock;

    /**
     * 已抢购数量（仅秒杀活动）
     */
    private Integer seckillSoldCount;

    /**
     * 每人限购（仅秒杀活动）
     */
    private Integer limitPerUser;

    /**
     * 商家信息VO
     */
    @Data
    public static class MerchantInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private String logo;
    }

    /**
     * 分类信息VO
     */
    @Data
    public static class CategoryInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private Long parentId;
        private Integer level;
    }
}
