package org.example.shoppingserver.model.dto.review;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价DTO
 */
@Data
public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long productId;
    private String userId;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer anonymous;
    private Integer likeCount;
    private Integer status;
    private String reply;
    private LocalDateTime createdAt;
}
