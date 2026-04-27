package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

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
 public ResponseResult<List<CartItemDTO>> getCartList( @RequestParam String userId) {
 List<CartItemDTO> cartList = cartService.getCartList(userId);
 return ResponseResult.success(cartList);
 }

 /**
 * 获取选中的购物车商品
 */
 @GetMapping("/checked")
 public ResponseResult<List<CartItemDTO>> getCheckedItems( @RequestParam String userId) {
 List<CartItemDTO> items = cartService.getCheckedItems(userId);
 return ResponseResult.success(items);
 }

 /**
 * 获取购物车商品数量
 */
 @GetMapping("/count")
 public ResponseResult<Integer> getCartCount( @RequestParam String userId) {
 Integer count = cartService.getCartCount(userId);
 return ResponseResult.success(count);
 }

 /**
 * 获取购物车统计信息
 */
 @GetMapping("/statistics")
 public ResponseResult<CartService.CartStatisticsDTO> getStatistics( @RequestParam String userId) {
 CartService.CartStatisticsDTO statistics = cartService.getStatistics(userId);
 return ResponseResult.success(statistics);
 }

 /**
 * 添加商品到购物车
 */
 @PostMapping
 public ResponseResult<CartItemDTO> addToCart( @RequestParam String userId,
 @RequestBody AddCartDTO addCartDTO) {
 CartItemDTO cartItem = cartService.addToCart(userId, addCartDTO);
 return ResponseResult.success(cartItem);
 }

 /**
 * 更新购物车商品数量
 */
 @PutMapping("/{cartItemId}/quantity")
 public ResponseResult<CartItemDTO> updateQuantity( @RequestParam String userId, @PathVariable Long cartItemId, @RequestParam Integer quantity) {
 CartItemDTO cartItem = cartService.updateQuantity(userId, cartItemId, quantity);
 return ResponseResult.success(cartItem);
 }

 /**
 * 选中/取消选中购物车商品
 */
 @PutMapping("/{cartItemId}/check")
 public ResponseResult<Boolean> checkItem( @RequestParam String userId, @PathVariable Long cartItemId, @RequestParam Integer checked) {
 boolean result = cartService.checkItem(userId, cartItemId, checked);
 return ResponseResult.success(result);
 }

 /**
 * 全选/取消全选
 */
 @PutMapping("/check-all")
 public ResponseResult<Boolean> checkAll( @RequestParam String userId, @RequestParam Integer checked) {
 boolean result = cartService.checkAll(userId, checked);
 return ResponseResult.success(result);
 }

 /**
 * 删除购物车商品
 */
 @DeleteMapping("/{cartItemId}")
 public ResponseResult<Boolean> deleteItem( @RequestParam String userId, @PathVariable Long cartItemId) {
 boolean result = cartService.deleteItem(userId, cartItemId);
 return ResponseResult.success(result);
 }

 /**
 * 批量删除购物车商品
 */
 @DeleteMapping("/batch")
 public ResponseResult<Boolean> batchDelete( @RequestParam String userId,
 @RequestBody Map<String, List<Long>> request) {
 List<Long> cartItemIds = request.get("cartItemIds");
 boolean result = cartService.batchDelete(userId, cartItemIds);
 return ResponseResult.success(result);
 }

 /**
 * 清空购物车
 */
 @DeleteMapping("/clear")
 public ResponseResult<Boolean> clearCart( @RequestParam String userId) {
 boolean result = cartService.clearCart(userId);
 return ResponseResult.success(result);
 }
}
