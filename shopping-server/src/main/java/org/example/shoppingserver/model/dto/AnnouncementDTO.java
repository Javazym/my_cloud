package org.example.shoppingserver.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;
}