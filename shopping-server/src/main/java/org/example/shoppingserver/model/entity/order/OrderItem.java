package org.example.shoppingserver.model.entity.order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.product.ProductSku;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.math.BigDecimal;

/**
 * 订单商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    /**
     * 订单ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 订单号
     */
    @Column(name = "order_no", nullable = false, length = 64)
    private String orderNo;

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
     * 商品名称（快照）
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 商品图片（快照）
     */
    @Column(name = "product_image", length = 255)
    private String productImage;

    /**
     * SKU规格（快照）
     */
    @Column(name = "sku_specs", length = 255)
    private String skuSpecs;

    /**
     * 商品单价（快照）
     */
    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 总价
     */
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * 评价状态：0-未评价，1-已评价
     */
    @Column(name = "review_status")
    private Integer reviewStatus = 0;

    /**
     * 计算小计
     */
    public void calculateTotalPrice() {
        this.totalPrice = this.productPrice.multiply(new BigDecimal(this.quantity));
    }
}
