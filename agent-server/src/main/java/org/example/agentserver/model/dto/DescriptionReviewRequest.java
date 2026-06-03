package org.example.agentserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DescriptionReviewRequest {
    @NotBlank(message = "商品描述不能为空")
    private String description;

    private String productName;
    private Double price;
    private Double originalPrice;
    private String categoryName;
    private String merchantName;
    private String tags;
}
