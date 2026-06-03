package org.example.shoppingserver.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.product.ProductCreateDTO;
import org.example.shoppingserver.model.dto.product.ProductQueryDTO;
import org.example.shoppingserver.model.dto.product.ProductUpdateDTO;
import org.example.shoppingserver.model.entity.common.Category;
import org.example.shoppingserver.model.vo.product.ProductDetailVO;
import org.example.shoppingserver.model.vo.product.ProductVO;
import org.example.shoppingserver.service.ProductService;
import org.example.shoppingserver.util.annotation.RequireRole;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

 private final ProductService productService;

 /**
 * 分页查询商品
 *
 * @param queryDTO 查询条件
 * @return 商品分页结果
 */
 @GetMapping
 public ResponseResult<Page<ProductVO>> getProducts(ProductQueryDTO queryDTO) {
 Page<ProductVO> page = productService.getProducts(queryDTO);
 return ResponseResult.success(page);
 }

 /**
 * 获取商品详情
 *
 * @param productId 商品ID
 * @return 商品详情
 */
 @GetMapping("/{productId}")
 public ResponseResult<ProductDetailVO> getProductDetail(@PathVariable Long productId) {
 ProductDetailVO product = productService.getProductDetail(productId);
 return ResponseResult.success(product);
 }

 /**
 * 获取热卖商品
 *
 * @param limit 数量限制
 * @return 热卖商品列表
 */
 @GetMapping("/hot")
 public ResponseResult<java.util.List<ProductVO>> getHotProducts(
 @RequestParam(defaultValue = "10") int limit) {
 java.util.List<ProductVO> products = productService.getHotProducts(limit);
 return ResponseResult.success(products);
 }

 /**
 * 获取精选商品
 *
 * @param limit 数量限制
 * @return 精选商品列表
 */
 @GetMapping("/featured")
 public ResponseResult<java.util.List<ProductVO>> getFeaturedProducts(
 @RequestParam(defaultValue = "10") int limit) {
 java.util.List<ProductVO> products = productService.getFeaturedProducts(limit);
 return ResponseResult.success(products);
 }

 /**
 * 获取新品
 *
 * @param limit 数量限制
 * @return 新品列表
 */
 @GetMapping("/new")
 public ResponseResult<java.util.List<ProductVO>> getNewProducts(
 @RequestParam(defaultValue = "10") int limit) {
 java.util.List<ProductVO> products = productService.getNewProducts(limit);
 return ResponseResult.success(products);
 }

 /**
 * 获取推荐商品
 *
 * @param limit 数量限制
 * @return 推荐商品列表
 */
 @GetMapping("/recommended")
 public ResponseResult<java.util.List<ProductVO>> getRecommendedProducts(
 @RequestParam(defaultValue = "10") int limit) {
 java.util.List<ProductVO> products = productService.getRecommendedProducts(limit);
 return ResponseResult.success(products);
 }

 /**
 * 搜索商品
 *
 * @param keyword 关键词
 * @param pageNum 页码
 * @param pageSize 每页数量
 * @return 商品分页结果
 */
 @GetMapping("/search")
 public ResponseResult<Page<ProductVO>> searchProducts(
 @RequestParam String keyword,
 @RequestParam(defaultValue = "1") int pageNum,
 @RequestParam(defaultValue = "10") int pageSize) {
 Page<ProductVO> page = productService.searchProducts(keyword, pageNum, pageSize);
 return ResponseResult.success(page);
 }

 /**
 * 获取商品分类列表
 * 
 * @deprecated 请使用 {@link CategoryController#getCategoryTree()} 获取分类树形结构
 * @return 分类列表
 */
 @Deprecated
 @GetMapping("/categories")
 public ResponseResult<List<Category>> getCategories() {
 List<Category> categories = productService.getCategories();
 return ResponseResult.success(categories);
 }

 /**
 * 获取商品分类详情
 * 
 * @deprecated 请使用 {@link CategoryController#getCategoryById(Long)} 获取分类详情
 * @param categoryId 分类ID
 * @return 分类详情
 */
 @Deprecated
 @GetMapping("/categories/{categoryId}")
 public ResponseResult<Category> getCategoryById(@PathVariable Long categoryId) {
 Category category = productService.getCategoryById(categoryId);
 return ResponseResult.success(category);
 }

 /**
 * 创建商品
 *
 * @param product 商品信息
 * @return 商品ID
 */
 @RequireRole(value = {"ROLE_MERCHANT"})
 @PostMapping
 public ResponseResult<Long> createProduct(@Valid @RequestBody ProductCreateDTO product) {
 Long productId = productService.createProduct(product);
 return ResponseResult.success(productId);
 }

 /**
 * 更新商品
 *
 * @param productId 商品ID
 * @param product 商品信息
 * @return 操作结果
 */
 @RequireRole(value = {"ROLE_MERCHANT"})
 @PutMapping("/{productId}")
 public ResponseResult<Void> updateProduct(
 @PathVariable Long productId,
 @Valid @RequestBody ProductUpdateDTO product) {
 productService.updateProduct(productId, product);
 return ResponseResult.success();
 }

 /**
 * 删除商品
 *
 * @param productId 商品ID
 * @return 操作结果
 */
 @DeleteMapping("/{productId}")
 public ResponseResult<Void> deleteProduct(@PathVariable Long productId) {
 productService.deleteProduct(productId);
 return ResponseResult.success();
 }
}
