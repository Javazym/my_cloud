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
