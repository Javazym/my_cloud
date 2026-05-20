package org.example.shoppingserver.model.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.model.entity.common.BaseEntity;
import org.example.shoppingserver.model.entity.order.Order;
import org.example.shoppingserver.model.entity.order.OrderItem;

/**
 * 商品评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_reviews")
public class ProductReview extends BaseEntity {

    /**
     * 订单ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 订单商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    /**
     * 商品ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 用户ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 用户名（脱敏）
     */
    @Column(name = "user_name", length = 50)
    private String userName;

    /**
     * 用户头像
     */
    @Column(name = "user_avatar", length = 255)
    private String userAvatar;

    /**
     * 评分：1-5
     */
    @Column(name = "rating", nullable = false)
    private Integer rating = 5;

    /**
     * 评价内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    /**
     * 是否匿名：0-否，1-是
     */
    @Column(name = "anonymous")
    private Integer anonymous = 0;

    /**
     * 点赞数
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * 状态：0-隐藏，1-显示
     */
    @Column(name = "status")
    private Integer status = 1;
}
