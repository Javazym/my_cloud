package org.example.agentserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.agentserver.model.dto.BatchReviewRequest;
import org.example.agentserver.model.dto.ReviewRequest;
import org.example.agentserver.service.ApiProxyService;
import org.example.agentserver.service.PendingProductService;
import org.example.agentserver.service.ProductReviewService;
import org.example.commonapi.dto.product.ProductSimpleVO;
import org.example.commonapi.dto.product.ProductVO;
import org.example.commonapi.result.ApiResult;
import org.example.commonapi.result.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ProxyController {

    private final ApiProxyService proxyService;
    private final ProductReviewService productReviewService;
    private final PendingProductService pendingProductService;

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

    @PostMapping("/review/batch/stream")
    public SseEmitter reviewBatchStream(@Valid @RequestBody BatchReviewRequest request) {
        return productReviewService.reviewBatchStream(request.getProductIds());
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

    /**
     * 获取待审核商品列表
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页数量
     * @return 待审核商品分页结果
     */
    @GetMapping("/products/pending")
    public ResponseResult<List<ProductVO>> getPendingProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        ResponseResult<List<ProductVO>> result = pendingProductService.getPendingProducts(pageNum, pageSize);
        return result;
    }
}
