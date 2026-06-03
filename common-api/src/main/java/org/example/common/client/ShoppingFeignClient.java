package org.example.common.client;

import org.example.common.dto.merchant.MerchantVO;
import org.example.common.dto.product.ProductSimpleVO;
import org.example.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 购物服务 Feign 客户端
 * 提供商户和商品相关的远程调用接口
 */
@FeignClient(name = "shopping-server")
public interface ShoppingFeignClient {

    /**
     * 根据用户ID获取商户信息
     *
     * @param userId 用户ID
     * @return 商户信息
     */
    @GetMapping("/merchants/by-user")
    ApiResult<MerchantVO> getMerchantByUserId(@RequestParam("userId") String userId);

    /**
     * 获取待审核商品列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 待审核商品分页列表
     */
    @GetMapping("/admin/products/pending")
    ApiResult<Page<ProductSimpleVO>> getPendingProducts(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    );
}
