package org.example.commonapi.client;

import org.example.commonapi.dto.product.ProductSimpleVO;
import org.example.commonapi.dto.product.ProductVO;
import org.example.commonapi.result.ApiResult;
import org.example.commonapi.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物服务 Feign 客户端
 * 提供商户和商品相关的远程调用接口
 */
@FeignClient(name = "shopping-server", url = "localhost:8901")
public interface ShoppingFeignClient {

    /**
     * 获取待审核商品列表
     *
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 待审核商品分页列表
     */
    @GetMapping("/admin/products/ai/pending")
    ResponseResult<List<ProductVO>> getPendingProducts(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    );
}
