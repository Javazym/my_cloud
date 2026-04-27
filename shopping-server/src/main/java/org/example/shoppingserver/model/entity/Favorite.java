package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "favorites")
public class Favorite extends BaseEntity {

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
}
