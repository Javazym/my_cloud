package org.example.agentserver.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ImageReviewRequest {
    @NotEmpty(message = "图片URL列表不能为空")
    private List<String> urls;
}
