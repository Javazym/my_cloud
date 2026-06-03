package org.example.agentserver.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}
