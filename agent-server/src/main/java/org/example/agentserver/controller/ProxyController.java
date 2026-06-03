package org.example.agentserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.agentserver.model.dto.ChatRequest;
import org.example.agentserver.model.dto.DescriptionReviewRequest;
import org.example.agentserver.model.dto.ImageReviewRequest;
import org.example.agentserver.model.dto.ProductReviewRequest;
import org.example.agentserver.service.ApiProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProxyController {

    private final ApiProxyService proxyService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(proxyService.get("/api/health"));
    }

    @PostMapping("/review/product")
    public ResponseEntity<Map<String, Object>> reviewProduct(
            @Valid @RequestBody ProductReviewRequest request) {
        return ResponseEntity.ok(proxyService.post("/api/review/product", request));
    }

    @PostMapping("/review/batch")
    public ResponseEntity<Map<String, Object>> reviewBatch(
            @RequestBody Map<String, List<ProductReviewRequest>> request) {
        return ResponseEntity.ok(proxyService.post("/api/review/batch", request));
    }

    @PostMapping("/review/description")
    public ResponseEntity<Map<String, Object>> reviewDescription(
            @Valid @RequestBody DescriptionReviewRequest request) {
        return ResponseEntity.ok(proxyService.post("/api/review/description", request));
    }

    @PostMapping("/review/images")
    public ResponseEntity<Map<String, Object>> reviewImages(
            @Valid @RequestBody ImageReviewRequest request) {
        return ResponseEntity.ok(proxyService.post("/api/review/images", request));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(proxyService.post("/api/chat", request));
    }
}
