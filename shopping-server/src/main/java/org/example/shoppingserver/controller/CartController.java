package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.AddCartDTO;
import org.example.shoppingserver.model.dto.CartItemDTO;
import org.example.shoppingserver.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
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
 */
 @GetMapping
 public ResponseResult<List<CartItemDTO>> getCartList() {
 List<CartItemDTO> cartList = cartService.getCartList(UserHolder.getCurrentUserId());
 return ResponseResult.success(cartList);
 }

 /**
 * 获取选中的购物车商品
 */
 @GetMapping("/checked")
 public ResponseResult<List<CartItemDTO>> getCheckedItems() {
 List<CartItemDTO> items = cartService.getCheckedItems(UserHolder.getCurrentUserId());
 return ResponseResult.success(items);
 }

 /**
 * 获取购物车商品数量
 */
 @GetMapping("/count")
 public ResponseResult<Integer> getCartCount() {
 Integer count = cartService.getCartCount(UserHolder.getCurrentUserId());
 return ResponseResult.success(count);
 }

 /**
 * 获取购物车统计信息
 */
 @GetMapping("/statistics")
 public ResponseResult<CartService.CartStatisticsDTO> getStatistics() {
 CartService.CartStatisticsDTO statistics = cartService.getStatistics(UserHolder.getCurrentUserId());
 return ResponseResult.success(statistics);
 }

 /**
 * 添加商品到购物车
 */
 @PostMapping
 public ResponseResult<CartItemDTO> addToCart(@RequestBody AddCartDTO addCartDTO) {
 CartItemDTO cartItem = cartService.addToCart(UserHolder.getCurrentUserId(), addCartDTO);
 return ResponseResult.success(cartItem);
 }

 /**
 * 更新购物车商品数量
 */
 @PutMapping("/{cartItemId}/quantity")
 public ResponseResult<CartItemDTO> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
 CartItemDTO cartItem = cartService.updateQuantity(UserHolder.getCurrentUserId(), cartItemId, quantity);
 return ResponseResult.success(cartItem);
 }

 /**
 * 选中/取消选中购物车商品
 */
 @PutMapping("/{cartItemId}/check")
 public ResponseResult<Boolean> checkItem(@PathVariable Long cartItemId, @RequestParam Integer checked) {
 boolean result = cartService.checkItem(UserHolder.getCurrentUserId(), cartItemId, checked);
 return ResponseResult.success(result);
 }

 /**
 * 全选/取消全选
 */
 @PutMapping("/check-all")
 public ResponseResult<Boolean> checkAll(@RequestParam Integer checked) {
 boolean result = cartService.checkAll(UserHolder.getCurrentUserId(), checked);
 return ResponseResult.success(result);
 }

 /**
 * 删除购物车商品
 */
 @DeleteMapping("/{cartItemId}")
 public ResponseResult<Boolean> deleteItem(@PathVariable Long cartItemId) {
 boolean result = cartService.deleteItem(UserHolder.getCurrentUserId(), cartItemId);
 return ResponseResult.success(result);
 }

 /**
 * 批量删除购物车商品
 */
 @DeleteMapping("/batch")
 public ResponseResult<Boolean> batchDelete(@RequestBody Map<String, List<Long>> request) {
 List<Long> cartItemIds = request.get("cartItemIds");
 boolean result = cartService.batchDelete(UserHolder.getCurrentUserId(), cartItemIds);
 return ResponseResult.success(result);
 }

 /**
 * 清空购物车
 */
 @DeleteMapping("/clear")
 public ResponseResult<Boolean> clearCart() {
 boolean result = cartService.clearCart(UserHolder.getCurrentUserId());
 return ResponseResult.success(result);
 }
}
