package org.example.shoppingserver.model.vo.review;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价VO
 */
@Data
public class ReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 评分 (1-5)
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片列表
     */
    private List<String> images;

    /**
     * 商家回复内容
     */
    private String replyContent;

    /**
     * 商家回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
