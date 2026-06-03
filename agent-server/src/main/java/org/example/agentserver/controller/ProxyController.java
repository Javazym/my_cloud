package org.example.agentserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.agentserver.model.dto.BatchReviewRequest;
import org.example.agentserver.model.dto.ReviewRequest;
import org.example.agentserver.service.ApiProxyService;
import org.example.agentserver.service.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ProxyController {

    private final ApiProxyService proxyService;
    private final ProductReviewService productReviewService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(proxyService.get("/api/health"));
    }

    @PostMapping("/review/product")
    public ResponseEntity<Map<String, Object>> reviewProduct(
            @Valid @RequestBody ReviewRequest request) {
        Map<String, Object> result = productReviewService.reviewProduct(request.getProductId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/review/batch")
    public ResponseEntity<Map<String, Object>> reviewBatch(
            @Valid @RequestBody BatchReviewRequest request) {
        Map<String, Object> result = productReviewService.reviewBatch(request.getProductIds());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/review/description")
    public ResponseEntity<Map<String, Object>> reviewDescription(
            @Valid @RequestBody ReviewRequest request) {
        Map<String, Object> result = productReviewService.reviewDescription(request.getProductId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/review/images")
    public ResponseEntity<Map<String, Object>> reviewImages(
            @Valid @RequestBody ReviewRequest request) {
        Map<String, Object> result = productReviewService.reviewImages(request.getProductId());
        return ResponseEntity.ok(result);
    }
}
