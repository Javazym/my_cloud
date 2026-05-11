package org.example.shoppingserver.util;

import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.product.ProductSku;
import org.example.shoppingserver.model.entity.product.ProductSpec;
import org.example.shoppingserver.model.entity.product.ProductSpecValue;
import org.example.shoppingserver.model.vo.product.ProductDetailVO;
import org.example.shoppingserver.model.vo.product.ProductSkuVO;
import org.example.shoppingserver.model.vo.product.ProductSpecVO;
import org.example.shoppingserver.model.vo.product.ProductSpecValueVO;
import org.example.shoppingserver.model.vo.product.ProductVO;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品对象转换器
 */
public class ProductConverter {

    /**
     * Product Entity 转 ProductVO
     */
    public static ProductVO toVO(Product product) {
        if (product == null) {
            return null;
        }

        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // 设置商家信息
        if (product.getMerchant() != null) {
            vo.setMerchantId(product.getMerchant().getId());
            vo.setMerchantName(product.getMerchant().getStoreName());
        }

        // 设置分类信息（只设置ID，名称需要额外查询）
        vo.setCategoryId(product.getCategoryId());

        // 设置SKU数量
        if (product.getSkus() != null) {
            vo.setSkuCount(product.getSkus().size());
        }

        return vo;
    }

    /**
     * Product Entity 列表转 ProductVO 列表
     */
    public static List<ProductVO> toVOList(List<Product> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream()
                .map(ProductConverter::toVO)
                .collect(Collectors.toList());
    }

    /**
     * Product Entity 转 ProductDetailVO
     */
    public static ProductDetailVO toDetailVO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 设置商家信息
        if (product.getMerchant() != null) {
            ProductDetailVO.MerchantInfoVO merchantVO = new ProductDetailVO.MerchantInfoVO();
            merchantVO.setId(product.getMerchant().getId());
            merchantVO.setName(product.getMerchant().getStoreName());
            merchantVO.setLogo(product.getMerchant().getStoreName());
            vo.setMerchant(merchantVO);
        }

        // 设置分类信息（只设置ID，详细信恔需要额外查询）


        // 转换标签为列表
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            vo.setTags(Arrays.asList(product.getTags().split(",")));
        }

        // 转换规格列表
        if (product.getSpecs() != null) {
            vo.setSpecs(product.getSpecs().stream()
                    .map(ProductConverter::toSpecVO)
                    .collect(Collectors.toList()));
        }

        // 转换SKU列表
        if (product.getSkus() != null) {
            vo.setSkus(product.getSkus().stream()
                    .map(ProductConverter::toSkuVO)
                    .collect(Collectors.toList()));
        }

        return vo;
    }

    /**
     * ProductSpec Entity 转 ProductSpecVO
     */
    public static ProductSpecVO toSpecVO(ProductSpec spec) {
        if (spec == null) {
            return null;
        }

        ProductSpecVO vo = new ProductSpecVO();
        BeanUtils.copyProperties(spec, vo);

        // 转换规格值列表
        if (spec.getValues() != null) {
            vo.setValues(spec.getValues().stream()
                    .map(ProductConverter::toSpecValueVO)
                    .collect(Collectors.toList()));
        }

        return vo;
    }

    /**
     * ProductSpecValue Entity 转 ProductSpecValueVO
     */
    public static ProductSpecValueVO toSpecValueVO(ProductSpecValue value) {
        if (value == null) {
            return null;
        }

        ProductSpecValueVO vo = new ProductSpecValueVO();
        BeanUtils.copyProperties(value, vo);
        return vo;
    }

    /**
     * ProductSku Entity 转 ProductSkuVO
     */
    public static ProductSkuVO toSkuVO(ProductSku sku) {
        if (sku == null) {
            return null;
        }

        ProductSkuVO vo = new ProductSkuVO();
        BeanUtils.copyProperties(sku, vo);

        // 设置库存预警状态
        vo.setLowStockStatus(sku.isLowStock());

        return vo;
    }
}
