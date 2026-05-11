package org.example.shoppingserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.cart.AddCartDTO;
import org.example.shoppingserver.model.vo.cart.CartItemVO;
import org.example.shoppingserver.model.vo.cart.CartStatisticsVO;
import org.example.shoppingserver.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取购物车列表
     *
     * @return 购物车商品列表
     */
    @GetMapping
    public ResponseResult<List<CartItemVO>> getCartList() {
        List<CartItemVO> cartList = cartService.getCartList(UserHolder.getCurrentUserId());
        return ResponseResult.success(cartList);
    }

    /**
     * 获取选中的购物车商品
     *
     * @return 选中的购物车商品列表
     */
    @GetMapping("/checked")
    public ResponseResult<List<CartItemVO>> getCheckedItems() {
        List<CartItemVO> items = cartService.getCheckedItems(UserHolder.getCurrentUserId());
        return ResponseResult.success(items);
    }

    /**
     * 获取购物车商品数量
     *
     * @return 商品数量
     */
    @GetMapping("/count")
    public ResponseResult<Integer> getCartCount() {
        Integer count = cartService.getCartCount(UserHolder.getCurrentUserId());
        return ResponseResult.success(count);
    }

    /**
     * 获取购物车统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseResult<CartStatisticsVO> getStatistics() {
        CartStatisticsVO statistics = cartService.getStatistics(UserHolder.getCurrentUserId());
        return ResponseResult.success(statistics);
    }

    /**
     * 添加商品到购物车
     *
     * @param addCartDTO 添加DTO
     * @return 购物车商品
     */
    @PostMapping
    public ResponseResult<CartItemVO> addToCart(@Valid @RequestBody AddCartDTO addCartDTO) {
        CartItemVO cartItem = cartService.addToCart(UserHolder.getCurrentUserId(), addCartDTO);
        return ResponseResult.success(cartItem);
    }

    /**
     * 更新购物车商品数量
     *
     * @param cartItemId 购物车商品ID
     * @param quantity   数量
     * @return 购物车商品
     */
    @PutMapping("/{cartItemId}/quantity")
    public ResponseResult<CartItemVO> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartItemVO cartItem = cartService.updateQuantity(UserHolder.getCurrentUserId(), cartItemId, quantity);
        return ResponseResult.success(cartItem);
    }

    /**
     * 选中/取消选中购物车商品
     *
     * @param cartItemId 购物车商品ID
     * @param checked    是否选中 (0-否, 1-是)
     * @return 是否成功
     */
    @PutMapping("/{cartItemId}/check")
    public ResponseResult<Boolean> checkItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer checked) {
        boolean result = cartService.checkItem(UserHolder.getCurrentUserId(), cartItemId, checked);
        return ResponseResult.success(result);
    }

    /**
     * 全选/取消全选
     *
     * @param checked 是否选中 (0-否, 1-是)
     * @return 是否成功
     */
    @PutMapping("/check-all")
    public ResponseResult<Boolean> checkAll(@RequestParam Integer checked) {
        boolean result = cartService.checkAll(UserHolder.getCurrentUserId(), checked);
        return ResponseResult.success(result);
    }

    /**
     * 删除购物车商品
     *
     * @param cartItemId 购物车商品ID
     * @return 是否成功
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseResult<Boolean> deleteItem(@PathVariable Long cartItemId) {
        boolean result = cartService.deleteItem(UserHolder.getCurrentUserId(), cartItemId);
        return ResponseResult.success(result);
    }

    /**
     * 批量删除购物车商品
     *
     * @param request 请求参数，包含cartItemIds列表
     * @return 是否成功
     */
    @DeleteMapping("/batch")
    public ResponseResult<Boolean> batchDelete(@RequestBody Map<String, List<Long>> request) {
        List<Long> cartItemIds = request.get("cartItemIds");
        boolean result = cartService.batchDelete(UserHolder.getCurrentUserId(), cartItemIds);
        return ResponseResult.success(result);
    }

    /**
     * 清空购物车
     *
     * @return 是否成功
     */
    @DeleteMapping("/clear")
    public ResponseResult<Boolean> clearCart() {
        boolean result = cartService.clearCart(UserHolder.getCurrentUserId());
        return ResponseResult.success(result);
    }
}
