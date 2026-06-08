package org.example.agentserver.service;

import lombok.RequiredArgsConstructor;
import org.example.commonapi.client.ShoppingFeignClient;
import org.example.commonapi.dto.product.ProductSimpleVO;
import org.example.commonapi.dto.product.ProductVO;
import org.example.commonapi.result.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 待审核商品服务
 * 通过 Feign 调用 shopping-server 获取待审核商品数据
 */
@Service
@RequiredArgsConstructor
public class PendingProductService {

    private final ShoppingFeignClient shoppingFeignClient;

    /**
     * 获取待审核商品列表
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页数量
     * @return 待审核商品分页结果
     */
    public ResponseResult<List<ProductVO>> getPendingProducts(int pageNum, int pageSize) {
        return shoppingFeignClient.getPendingProducts(pageNum, pageSize);
    }
}
