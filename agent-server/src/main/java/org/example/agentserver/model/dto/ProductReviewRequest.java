package org.example.agentserver.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProductReviewRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    private String subName;
    private List<String> imageUrls;
    private String merchantName;
    private String categoryName;
    private PriceInfo priceInfo;
    private Integer stock;
    private Integer soldCount;
    private Integer reviewCount;
    private Integer favoriteCount;
    private Double rating;
    private String tags;
    private String keywords;
    private Boolean isHot;
    private Boolean isFeatured;
    private Boolean isNew;
    @Valid
    private Activity activity;

    @Data
    public static class PriceInfo {
        private Double price;
        private Double originalPrice;
    }

    @Data
    public static class Activity {
        private Boolean hasActivity;
        private Integer activityType;
        private Integer activityId;
        private String activityName;
        private Double activityPrice;
        private String activityStartTime;
        private String activityEndTime;
        private Integer activityStatus;
    }
}
