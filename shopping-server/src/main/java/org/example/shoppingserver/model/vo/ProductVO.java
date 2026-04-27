package org.example.shoppingserver.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商品VO - 用于列表展示
 */
@Data
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long merchantId;
    private String merchantName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String subName;
    private String image;
    private Map<Integer, String> images;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer soldCount;
    private Integer reviewCount;
    private Integer favoriteCount;
    private BigDecimal rating;
    private Integer isHot;
    private Integer isFeatured;
    private Integer isNew;
    private Integer publishStatus;
    private String tags;
    private String keywords;
    private Integer skuCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
