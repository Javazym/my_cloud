package org.example.shoppingserver.model.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.util.JsonListConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    /**
     * 商家ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 分类ID
     */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    /**
     * 商品名称
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 副标题
     */
    @Column(name = "sub_name", length = 200)
    private String subName;

    /**
     * 主图
     */
    @Column(name = "image", length = 255)
    private String image;

    /**
     * 图册（JSON数组）
     */
    @Column(name = "images", columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<String> images;

    /**
     * 价格
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 原价
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 成本价
     */
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    /**
     * 库存
     */
    @Column(name = "stock")
    private Integer stock = 0;

    /**
     * 销量
     */
    @Column(name = "sold_count")
    private Integer soldCount = 0;

    /**
     * 评价数
     */
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    /**
     * 收藏数
     */
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

    /**
     * 评分
     */
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = new BigDecimal("5.0");

    /**
     * 商品描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 商品详情（HTML）
     */
    @Column(name = "detail", columnDefinition = "LONGTEXT")
    private String detail;

    /**
     * 是否热卖：0-否，1-是
     */
    @Column(name = "is_hot")
    private Integer isHot = 0;

    /**
     * 是否精选：0-否，1-是
     */
    @Column(name = "is_featured")
    private Integer isFeatured = 0;

    /**
     * 是否新品：0-否，1-是
     */
    @Column(name = "is_new")
    private Integer isNew = 0;

    /**
     * 上架状态：0-下架，1-上架
     */
    @Column(name = "publish_status")
    private Integer publishStatus = 0;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "audit_status")
    private AuditStatus auditStatus = AuditStatus.PENDING;

    /**
     * 审核备注
     */
    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 标签（JSON数组）
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 关键词
     */
    @Column(name = "keywords", length = 255)
    private String keywords;

    /**
     * SKU列表
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSku> skus = new ArrayList<>();

    /**
     * 规格列表
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpec> specs = new ArrayList<>();

    /**
     * 扣减库存
     */
    public boolean deductStock(Integer quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return true;
        }
        return false;
    }

    /**
     * 增加库存
     */
    public void addStock(Integer quantity) {
        this.stock += quantity;
    }

    /**
     * 增加销量
     */
    public void addSoldCount(Integer quantity) {
        this.soldCount += quantity;
    }

    /**
     * 添加SKU
     */
    public void addSku(ProductSku sku) {
        skus.add(sku);
        sku.setProduct(this);
    }

    /**
     * 添加规格
     */
    public void addSpec(ProductSpec spec) {
        specs.add(spec);
        spec.setProduct(this);
    }
}
