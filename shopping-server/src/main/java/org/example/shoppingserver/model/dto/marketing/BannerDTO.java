package org.example.shoppingserver.model.dto.marketing;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerDTO {
    private Long id;
    private String title;
    private String image;
    private String imageUrl; // 兼容字段
    private String link;
    private String linkUrl; // 兼容字段
    private String linkType;
    private Integer position;
    private Integer sort;
    private Integer status; // 状态：0-禁用，1-启用
    private Integer clickCount; // 点击次数
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}