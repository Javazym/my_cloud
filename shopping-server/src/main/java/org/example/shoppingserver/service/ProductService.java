package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.product.ProductCreateDTO;
import org.example.shoppingserver.model.dto.product.ProductQueryDTO;
import org.example.shoppingserver.model.dto.product.ProductUpdateDTO;
import org.example.shoppingserver.model.entity.common.Category;
import org.example.shoppingserver.model.vo.product.ProductDetailVO;
import org.example.shoppingserver.model.vo.product.ProductVO;
import org.springframework.data.domain.Page;

/**
 * 商品Service接口
 */
public interface ProductService {

    /**
     * 创建商品
     *
     * @param dto 商品创建DTO
     * @return 商品ID
     */
    Long createProduct(ProductCreateDTO dto);

    /**
     * 更新商品
     *
     * @param productId 商品ID
     * @param dto 商品更新DTO
     */
    void updateProduct(Long productId, ProductUpdateDTO dto);

    /**
     * 删除商品
     *
     * @param productId 商品ID
     */
    void deleteProduct(Long productId);
    /**
     * 分页查询商品
     *
     * @param queryDTO 查询DTO
     * @return 商品分页结果
     */
    Page<ProductVO> getProducts(ProductQueryDTO queryDTO);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductDetailVO getProductDetail(Long productId);

    /**
     * 获取热卖商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    java.util.List<ProductVO> getHotProducts(int limit);

    /**
     * 获取精选商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    java.util.List<ProductVO> getFeaturedProducts(int limit);

    /**
     * 获取新品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    java.util.List<ProductVO> getNewProducts(int limit);

    /**
     * 获取推荐商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    java.util.List<ProductVO> getRecommendedProducts(int limit);

    /**
     * 搜索商品
     *
     * @param keyword  关键字
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 商品分页结果
     */
    Page<ProductVO> searchProducts(String keyword, int pageNum, int pageSize);

    /**
     * 获取商品分类列表
     *
     * @return 分类列表
     */
    java.util.List<Category> getCategories();

    /**
     * 获取商品分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    Category getCategoryById(Long categoryId);
}
