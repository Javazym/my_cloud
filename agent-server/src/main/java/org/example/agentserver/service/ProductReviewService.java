package org.example.agentserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.agentserver.model.dto.DescriptionReviewRequest;
import org.example.agentserver.model.dto.ImageReviewRequest;
import org.example.agentserver.model.dto.ProductReviewRequest;
import org.example.agentserver.repository.CategoryRepository;
import org.example.agentserver.repository.ProductRepository;
import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.common.Category;
import org.example.shoppingserver.model.entity.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ApiProxyService proxyService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ProductReviewRequest buildReviewRequest(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        ProductReviewRequest request = new ProductReviewRequest();
        request.setName(product.getName());
        request.setDescription(product.getDescription());
        request.setSubName(product.getSubName());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            request.setImageUrls(product.getImages());
        }

        if (product.getMerchant() != null) {
            request.setMerchantName(product.getMerchant().getStoreName());
        }

        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        request.setCategoryName(category != null ? category.getName() : null);

        if (product.getPrice() != null) {
            ProductReviewRequest.PriceInfo priceInfo = new ProductReviewRequest.PriceInfo();
            priceInfo.setPrice(product.getPrice().doubleValue());
            if (product.getOriginalPrice() != null) {
                priceInfo.setOriginalPrice(product.getOriginalPrice().doubleValue());
            }
            request.setPriceInfo(priceInfo);
        }

        request.setStock(product.getStock());
        request.setSoldCount(product.getSoldCount());
        request.setReviewCount(product.getReviewCount());
        request.setFavoriteCount(product.getFavoriteCount());
        request.setRating(product.getRating() != null ? product.getRating().doubleValue() : null);
        request.setTags(product.getTags());
        request.setKeywords(product.getKeywords());
        request.setIsHot(product.getIsHot() != null && product.getIsHot() == 1);
        request.setIsFeatured(product.getIsFeatured() != null && product.getIsFeatured() == 1);
        request.setIsNew(product.getIsNew() != null && product.getIsNew() == 1);

        return request;
    }

    @Transactional
    public Map<String, Object> reviewProduct(Long productId) {
        ProductReviewRequest request = buildReviewRequest(productId);
        Map<String, Object> result = proxyService.post("/api/review/product", request);
        updateProductAuditStatus(productId, result);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> reviewDescription(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        DescriptionReviewRequest request = new DescriptionReviewRequest();
        request.setDescription(product.getDescription());
        request.setProductName(product.getName());
        request.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : null);
        request.setOriginalPrice(product.getOriginalPrice() != null ? product.getOriginalPrice().doubleValue() : null);

        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        request.setCategoryName(category != null ? category.getName() : null);

        if (product.getMerchant() != null) {
            request.setMerchantName(product.getMerchant().getStoreName());
        }

        request.setTags(product.getTags());

        return proxyService.post("/api/review/description", request);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> reviewImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        ImageReviewRequest request = new ImageReviewRequest();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            request.setUrls(product.getImages());
        }

        return proxyService.post("/api/review/images", request);
    }

    @Transactional
    public Map<String, Object> reviewBatch(List<Long> productIds) {
        Map<String, List<ProductReviewRequest>> batchRequest = new java.util.HashMap<>();
        List<ProductReviewRequest> requests = productIds.stream()
                .map(this::buildReviewRequest)
                .toList();
        batchRequest.put("products", requests);
        Map<String, Object> result = proxyService.post("/api/review/batch", batchRequest);
        updateBatchAuditStatus(productIds, result);
        return result;
    }

    @Transactional
    protected void updateProductAuditStatus(Long productId, Map<String, Object> result) {
        if (result == null) return;
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        String conclusion = (String) result.get("conclusion");
        if ("通过".equals(conclusion)) {
            product.setAuditStatus(AuditStatus.APPROVED);
        } else if ("不通过".equals(conclusion)) {
            product.setAuditStatus(AuditStatus.REJECTED);
        } else {
            product.setAuditStatus(AuditStatus.PENDING);
        }

        Object suggestion = result.get("suggestion");
        if (suggestion != null) {
            product.setAuditRemark(suggestion.toString());
        }

        product.setAuditTime(LocalDateTime.now());
        productRepository.save(product);
    }

    @SuppressWarnings("unchecked")
    private void updateBatchAuditStatus(List<Long> productIds, Map<String, Object> result) {
        if (result == null) return;
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        if (products == null) return;

        for (int i = 0; i < products.size() && i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Map<String, Object> productResult = products.get(i);
            updateProductAuditStatus(productId, productResult);
        }
    }
}
