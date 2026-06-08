package org.example.commonapi.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品信息 VO
 * 用于微服务间传递商品数据（简化版，用于审核列表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 忽略未知字段（如 activityType、createdAt 等）
public class ProductSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subName;

    /**
     * 主图
     */
    private String image;

    /**
     * 商品图片列表
     */
    private List<String> images;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer soldCount;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 上架状态：0-下架，1-上架
     */
    private Integer publishStatus;

    /**
     * 审核状态：0-待审核，1-通过，2-驳回
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 是否热门：0-否，1-是
     */
    private Integer isHot;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isFeatured;

    /**
     * 是否新品：0-否，1-是
     */
    private Integer isNew;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 评论数
     */
    private Integer reviewCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;
}
