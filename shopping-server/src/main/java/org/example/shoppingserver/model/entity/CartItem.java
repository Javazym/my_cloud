package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * SKU ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    private ProductSku sku;

    /**
     * 数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * 是否选中：0-否，1-是
     */
    @Column(name = "checked")
    private Integer checked = 1;

    /**
     * 获取小计
     */
    public java.math.BigDecimal getSubtotal() {
        if (sku != null) {
            return sku.getPrice().multiply(new java.math.BigDecimal(quantity));
        }
        return product.getPrice().multiply(new java.math.BigDecimal(quantity));
    }
}
