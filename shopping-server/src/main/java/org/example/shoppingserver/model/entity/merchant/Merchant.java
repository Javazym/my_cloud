package org.example.shoppingserver.model.entity.merchant;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.util.JsonListConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "merchants")
public class Merchant extends BaseEntity {

    /**
     * 关联用户ID
     */
    @Column(name = "user_id", nullable = false, length = 100, unique = true)
    private String userId;

    @OneToOne(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MerchantAccount account;

    /**
     * 店铺名称
     */
    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    /**
     * 店铺Logo
     */
    @Column(name = "store_logo", length = 255)
    private String storeLogo;

    /**
     * 店铺横幅
     */
    @Column(name = "store_banner", length = 255)
    private String storeBanner;

    /**
     * 店铺简介
     */
    @Column(name = "store_description", columnDefinition = "TEXT")
    private String storeDescription;

    /**
     * 店铺类型：normal-普通，flagship-旗舰店
     */
    @Column(name = "store_type", length = 20)
    private String storeType = "normal";

    /**
     * 主营类目
     */
    @Column(name = "category", length = 50)
@Convert(converter = JsonListConverter.class)
    private List<String> category;

    /**
     * 联系人
     */
    @Column(name = "contact_name", length = 50)
    private String contactName;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 店铺评分
     */
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = new BigDecimal("5.0");

    /**
     * 总销售额
     */
    @Column(name = "sales")
    private Long sales = 0L;

    /**
     * 关注数
     */
    @Column(name = "followers")
    private Integer followers = 0;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "audit_status")
    private AuditStatus auditStatus = AuditStatus.PENDING;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 商品列表
     */
    @OneToMany(mappedBy = "merchant")
    private List<Product> products = new ArrayList<>();

    /**
     * 添加商品
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setMerchant(this);
    }
    public void initAccount() {
        MerchantAccount account = new MerchantAccount();
        account.setMerchant(this);  // 这里必须设置！
        this.account = account;
    }
}
