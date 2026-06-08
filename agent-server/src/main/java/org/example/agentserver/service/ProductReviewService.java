package org.example.agentserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.example.commonapi.client.ShoppingFeignClient;
import org.example.commonapi.dto.product.ProductSimpleVO;
import org.example.commonapi.dto.product.ProductVO;
import org.example.commonapi.result.ApiResult;
import org.example.commonapi.result.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.example.agentserver.model.dto.DescriptionReviewRequest;
import org.example.agentserver.model.dto.ImageReviewRequest;
import org.example.agentserver.model.dto.ProductReviewRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private static final Logger log = LoggerFactory.getLogger(ProductReviewService.class);
    private final ShoppingFeignClient shoppingFeignClient;
    private final ApiProxyService proxyService;
    
    // 使用支持中文日期格式的 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule()
            .addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

    public ProductReviewRequest buildReviewRequest(Long productId) {
        // 通过 Feign Client 获取商品数据
        ResponseResult<List<ProductVO>> result = shoppingFeignClient.getPendingProducts(1, 100);
        if (result.getCode() != 1000 || result.getData() == null) {
            throw new RuntimeException("获取商品数据失败: " + productId);
        }

        List<ProductVO> page = result.getData();

        // 4. 查找商品
        ProductVO productVO =
                page.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
        
        return convertToReviewRequest(productVO);
    }

    public Map<String, Object> reviewProduct(Long productId) {
        ProductReviewRequest request = buildReviewRequest(productId);
        log.info("数据：{}", request);
        Map<String, Object> result = proxyService.post("/api/review/product", request);
        return result;
    }

    public Map<String, Object> reviewDescription(Long productId) {
        ProductReviewRequest request = buildReviewRequest(productId);
        
        DescriptionReviewRequest descRequest = new DescriptionReviewRequest();
        descRequest.setDescription(request.getDescription());
        descRequest.setProductName(request.getName());
        if (request.getPriceInfo() != null) {
            descRequest.setPrice(request.getPriceInfo().getPrice());
            descRequest.setOriginalPrice(request.getPriceInfo().getOriginalPrice());
        }
        descRequest.setCategoryName(request.getCategoryName());
        descRequest.setMerchantName(request.getMerchantName());
        descRequest.setTags(request.getTags());

        return proxyService.post("/api/review/description", descRequest);
    }

    public Map<String, Object> reviewImages(Long productId) {
        ProductReviewRequest request = buildReviewRequest(productId);
        log.info("数据：{}", request);
        ImageReviewRequest imageRequest = new ImageReviewRequest();
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            imageRequest.setUrls(request.getImageUrls());
        }

        return proxyService.post("/api/review/images", imageRequest);
    }

    public Map<String, Object> reviewBatch(List<Long> productIds) {
        Map<String, List<ProductReviewRequest>> batchRequest = new java.util.HashMap<>();
        List<ProductReviewRequest> requests = productIds.stream()
                .map(this::buildReviewRequest)
                .toList();
        batchRequest.put("products", requests);
        Map<String, Object> result = proxyService.post("/api/review/batch", batchRequest);
        return result;
    }
    
    /**
     * 将 ProductSimpleVO 转换为 ProductReviewRequest
     */
    private ProductReviewRequest convertToReviewRequest(ProductVO product) {
        ProductReviewRequest request = new ProductReviewRequest();
        request.setName(product.getName());
        request.setDescription(product.getDescription());
        request.setSubName(product.getSubName());
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            request.setImageUrls(product.getImages());
        }
        
        request.setMerchantName(product.getMerchantName());
        request.setCategoryName(product.getCategoryName());
        
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
        if (product.getRating() != null) {
            request.setRating(product.getRating().doubleValue());
        }
        request.setTags(product.getTags() != null ? String.join(",", product.getTags()) : null);
        request.setKeywords(product.getKeywords());
        request.setIsHot(product.getIsHot() != null && product.getIsHot() == 1);
        request.setIsFeatured(product.getIsFeatured() != null && product.getIsFeatured() == 1);
        request.setIsNew(product.getIsNew() != null && product.getIsNew() == 1);
        
        return request;
    }
    
}
