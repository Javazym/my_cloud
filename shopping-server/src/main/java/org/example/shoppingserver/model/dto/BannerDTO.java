package org.example.shoppingserver.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerDTO {
    private Long id;
    private String title;
    private String image;
    private String link;
    private String linkType;
    private Integer position;
    private Integer sort;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}