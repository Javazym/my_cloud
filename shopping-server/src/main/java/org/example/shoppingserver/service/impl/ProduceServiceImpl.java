package org.example.shoppingserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.ProductCreateDTO;
import org.example.shoppingserver.model.dto.ProductQueryDTO;
import org.example.shoppingserver.model.dto.ProductSkuCreateDTO;
import org.example.shoppingserver.model.dto.ProductSpecDTO;
import org.example.shoppingserver.model.dto.ProductSpecValueDTO;
import org.example.shoppingserver.model.dto.ProductUpdateDTO;
import org.example.shoppingserver.model.entity.*;
import org.example.shoppingserver.model.vo.ProductDetailVO;
import org.example.shoppingserver.model.vo.ProductVO;
import org.example.shoppingserver.repository.CategoryRepository;
import org.example.shoppingserver.repository.MerchantRepository;
import org.example.shoppingserver.repository.ProductRepository;
import org.example.shoppingserver.service.ProductService;
import org.example.shoppingserver.util.ProductConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduceServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public Long createProduct(ProductCreateDTO dto) {
        Product product = new Product();
        convertToEntity(product, dto);

        // 设置商家信息
        Merchant merchant = merchantRepository.findByUserId(UserHolder.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        product.setMerchant(merchant);

        // 绑定关系，清空ID
        if (CollUtil.isNotEmpty(product.getSkus())) {
            product.getSkus().forEach(sku -> {
                sku.setId(null);
                sku.setProduct(product);
            });
        }
        if (CollUtil.isNotEmpty(product.getSpecs())) {
            product.getSpecs().forEach(spec -> {
                spec.setId(null);
                spec.setProduct(product);
            });
        }

        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, ProductUpdateDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        product.getSkus().clear();
        product.getSpecs().clear();

        convertToUpdateEntity(product, dto);

        if (CollUtil.isNotEmpty(product.getSkus())) {
            product.getSkus().forEach(sku -> {
                sku.setId(null);
                sku.setProduct(product);
            });
        }
        if (CollUtil.isNotEmpty(product.getSpecs())) {
            product.getSpecs().forEach(spec -> {
                spec.setId(null);
                spec.setProduct(product);
            });
        }

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        productRepository.delete(product);
    }

    private void convertToEntity(Product product, ProductCreateDTO dto) {
        product.setName(dto.getName());
        product.setSubName(dto.getSubName());
        product.setImage(dto.getImage());
        product.setImages(dto.getImages());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        product.setDetail(dto.getDetail());
        product.setIsHot(dto.getIsHot());
        product.setIsFeatured(dto.getIsFeatured());
        product.setIsNew(dto.getIsNew());
        product.setPublishStatus(dto.getPublishStatus());
        product.setTags(dto.getTags());
        product.setKeywords(dto.getKeywords());

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        product.setCategoryId(category.getId());

        if (dto.getSpecs() != null && !dto.getSpecs().isEmpty()) {
            List<ProductSpec> specs = dto.getSpecs().stream()
                    .map(this::convertSpecDtoToEntity)
                    .collect(Collectors.toList());
            product.setSpecs(specs);
        }

        if (dto.getSkus() != null && !dto.getSkus().isEmpty()) {
            List<ProductSku> skus = dto.getSkus().stream()
                    .map(this::convertSkuDtoToEntity)
                    .collect(Collectors.toList());
            product.setSkus(skus);
        }
    }

    private void convertToUpdateEntity(Product product, ProductUpdateDTO dto) {
        product.setName(dto.getName());
        product.setSubName(dto.getSubName());
        product.setImage(dto.getImage());
        product.setImages(dto.getImages());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        product.setDetail(dto.getDetail());

        if (dto.getIsHot() != null) {
            product.setIsHot(dto.getIsHot());
        }
        if (dto.getIsFeatured() != null) {
            product.setIsFeatured(dto.getIsFeatured());
        }
        if (dto.getIsNew() != null) {
            product.setIsNew(dto.getIsNew());
        }
        if (dto.getPublishStatus() != null) {
            product.setPublishStatus(dto.getPublishStatus());
        }
        if (dto.getTags() != null) {
            product.setTags(dto.getTags());
        }
        if (dto.getKeywords() != null) {
            product.setKeywords(dto.getKeywords());
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        product.setCategoryId(category.getId());

        if (dto.getSpecs() != null && !dto.getSpecs().isEmpty()) {
            List<ProductSpec> specs = dto.getSpecs().stream()
                    .map(this::convertSpecDtoToEntity)
                    .collect(Collectors.toList());
            product.setSpecs(specs);
        }

        if (dto.getSkus() != null && !dto.getSkus().isEmpty()) {
            List<ProductSku> skus = dto.getSkus().stream()
                    .map(this::convertSkuDtoToEntity)
                    .collect(Collectors.toList());
            product.setSkus(skus);
        }
    }

    // ====================== 【核心修复】关联 spec ======================
    private ProductSpec convertSpecDtoToEntity(ProductSpecDTO specDTO) {
        ProductSpec spec = new ProductSpec();
        spec.setId(null);
        spec.setName(specDTO.getName());
        spec.setSort(specDTO.getSort());

        if (specDTO.getValues() != null && !specDTO.getValues().isEmpty()) {
            List<ProductSpecValue> values = specDTO.getValues().stream()
                    .map(this::convertSpecValueDtoToEntity)
                    .collect(Collectors.toList());

            // 给每个值设置所属规格 → 自动填充 spec_id
            values.forEach(v -> v.setSpec(spec));

            spec.setValues(values);
        }

        return spec;
    }

    private ProductSpecValue convertSpecValueDtoToEntity(ProductSpecValueDTO valueDTO) {
        ProductSpecValue value = new ProductSpecValue();
        value.setId(null);
        value.setValue(valueDTO.getValue());
        value.setSort(valueDTO.getSort());
        return value;
    }
    // =================================================================

    private ProductSku convertSkuDtoToEntity(ProductSkuCreateDTO skuDTO) {
        ProductSku sku = new ProductSku();
        sku.setId(null);
        sku.setSkuCode(skuDTO.getSkuCode());
        sku.setSpecs(skuDTO.getSpecs());
        sku.setPrice(skuDTO.getPrice());
        sku.setOriginalPrice(skuDTO.getOriginalPrice());
        sku.setStock(skuDTO.getStock());
        sku.setLowStock(skuDTO.getLowStock());
        sku.setImage(skuDTO.getImage());
        sku.setStatus(skuDTO.getStatus());
        return sku;
    }

    @Override
    public Page<ProductVO> getProducts(ProductQueryDTO queryDTO) {
        Sort sort = Sort.unsorted();
        if (StrUtil.isNotBlank(queryDTO.getSortField())) {
            Sort.Direction direction = "asc".equalsIgnoreCase(queryDTO.getSortOrder())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, queryDTO.getSortField());
        }

        Pageable pageable = PageRequest.of(
                queryDTO.getPageNum() - 1,
                queryDTO.getPageSize(),
                sort
        );

        Page<Product> page = productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
                String kw = "%" + queryDTO.getKeyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), kw),
                        cb.like(root.get("keywords"), kw)
                ));
            }

            if (queryDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), queryDTO.getCategoryId()));
            }

            if (queryDTO.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), queryDTO.getBrandId()));
            }

            if (queryDTO.getMerchantId() != null) {
                predicates.add(cb.equal(root.get("merchant").get("id"), queryDTO.getMerchantId()));
            }

            if (queryDTO.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), queryDTO.getMinPrice()));
            }
            if (queryDTO.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), queryDTO.getMaxPrice()));
            }

            if (queryDTO.getRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), queryDTO.getRating()));
            }

            if (queryDTO.getIsHot() != null && queryDTO.getIsHot() == 1) {
                predicates.add(cb.equal(root.get("isHot"), 1));
            }
            if (queryDTO.getIsFeatured() != null && queryDTO.getIsFeatured() == 1) {
                predicates.add(cb.equal(root.get("isFeatured"), 1));
            }
            if (queryDTO.getIsNew() != null && queryDTO.getIsNew() == 1) {
                predicates.add(cb.equal(root.get("isNew"), 1));
            }
            if (queryDTO.getPublishStatus() != null) {
                predicates.add(cb.equal(root.get("publishStatus"), queryDTO.getPublishStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        return page.map(ProductConverter::toVO);
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return ProductConverter.toDetailVO(product);
    }

    @Override
    public List<ProductVO> getHotProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findHotProducts(pageable);
        return ProductConverter.toVOList(products);
    }

    @Override
    public List<ProductVO> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findFeaturedProducts(pageable);
        return ProductConverter.toVOList(products);
    }

    @Override
    public List<ProductVO> getNewProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findNewProducts(pageable);
        return ProductConverter.toVOList(products);
    }

    @Override
    public List<ProductVO> getRecommendedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findRecommendedProducts(pageable);
        return ProductConverter.toVOList(products);
    }

    @Override
    public Page<ProductVO> searchProducts(String keyword, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> page = productRepository.searchProducts(keyword, pageable);
        return page.map(ProductConverter::toVO);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findTopCategories();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

}