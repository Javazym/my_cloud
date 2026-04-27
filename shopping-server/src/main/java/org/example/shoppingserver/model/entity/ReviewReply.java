package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评价回复实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "review_replies")
public class ReviewReply extends BaseEntity {

    /**
     * 评价ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ProductReview review;

    /**
     * 商家ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 回复内容
     */
    @Column(name = "content", nullable = false, length = 500)
    private String content;
}
