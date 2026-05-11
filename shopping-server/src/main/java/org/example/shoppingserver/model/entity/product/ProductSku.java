package org.example.shoppingserver.model.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.util.JsonMapConverter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品SKU实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_skus")
public class ProductSku extends BaseEntity {

    /**
     * 商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * SKU编码
     */
    @Column(name = "sku_code", nullable = false, unique = true, length = 64)
    private String skuCode;

    /**
     * 规格组合（JSON）
     */
    @Column(name = "specs", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, String> specs;

    /**
     * 销售价
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 原价
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    @Column(name = "stock")
    private Integer stock = 0;

    /**
     * 库存预警值
     */
    @Column(name = "low_stock")
    private Integer lowStock = 10;

    /**
     * SKU图片
     */
    @Column(name = "image", length = 255)
    private String image;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;

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
     * 是否库存不足
     */
    public boolean isLowStock() {
        return stock <= lowStock;
    }
}
