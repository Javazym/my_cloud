package org.example.shoppingserver.model.dto.product;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品查询DTO
 */
@Data
public class ProductQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 价格区间-最低价
     */
    private java.math.BigDecimal minPrice;

    /**
     * 价格区间-最高价
     */
    private java.math.BigDecimal maxPrice;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 是否热卖：0-全部，1-热卖
     */
    private Integer isHot;

    /**
     * 是否精选：0-全部，1-精选
     */
    private Integer isFeatured;

    /**
     * 是否新品：0-全部，1-新品
     */
    private Integer isNew;

    /**
     * 上架状态
     */
    private Integer publishStatus;

    /**
     * 排序字段：price-价格，sales-销量，rating-评分，createdAt-创建时间
     */
    private String sortField;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
