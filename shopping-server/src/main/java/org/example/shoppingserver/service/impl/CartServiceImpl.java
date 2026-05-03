package org.example.shoppingserver.service.impl;

import org.example.shoppingserver.model.dto.AddCartDTO;
import org.example.shoppingserver.model.dto.CartItemDTO;
import org.example.shoppingserver.model.entity.CartItem;
import org.example.shoppingserver.repository.CartItemRepository;
import org.example.shoppingserver.repository.ProductRepository;
import org.example.shoppingserver.repository.ProductSkuRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductSkuRepository productSkuRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Cacheable(value = "cartList", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<CartItemDTO> getCartList(String userId) {
        return cartItemRepository.findByUserId(userId).stream().map(
                cartItem -> {
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    // 基本信息
                    cartItemDTO.setId(cartItem.getId());
                    cartItemDTO.setUserId(cartItem.getUser().getId());
                    cartItemDTO.setChecked(cartItem.getChecked());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    
                    // 商品信息
                    if (cartItem.getProduct() != null) {
                        cartItemDTO.setProductId(cartItem.getProduct().getId());
                        cartItemDTO.setProductName(cartItem.getProduct().getName());
                        cartItemDTO.setProductImage(cartItem.getProduct().getImage());
                        cartItemDTO.setPrice(cartItem.getProduct().getPrice());
                        cartItemDTO.setStock(cartItem.getProduct().getStock());
                        cartItemDTO.setValid(cartItem.getProduct().getPublishStatus() == 1);
                    }
                    
                    // SKU信息
                    if (cartItem.getSku() != null) {
                        cartItemDTO.setSkuId(cartItem.getSku().getId());
                        cartItemDTO.setSkuSpecs(cartItem.getSku().getSpecs() != null ? 
                            cartItem.getSku().getSpecs().toString() : null);
                        cartItemDTO.setPrice(cartItem.getSku().getPrice());
                        cartItemDTO.setStock(cartItem.getSku().getStock());
                    }
                    
                    // 计算小计
                    cartItemDTO.setSubtotal(cartItem.getSubtotal());
                    
                    return cartItemDTO;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<CartItemDTO> getCheckedItems(String userId) {
        return cartItemRepository.findCheckedItems(userId).stream().map(
                cartItem -> {
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    // 基本信息
                    cartItemDTO.setId(cartItem.getId());
                    cartItemDTO.setUserId(cartItem.getUser().getId());
                    cartItemDTO.setChecked(cartItem.getChecked());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    
                    // 商品信息
                    if (cartItem.getProduct() != null) {
                        cartItemDTO.setProductId(cartItem.getProduct().getId());
                        cartItemDTO.setProductName(cartItem.getProduct().getName());
                        cartItemDTO.setProductImage(cartItem.getProduct().getImage());
                        cartItemDTO.setPrice(cartItem.getProduct().getPrice());
                        cartItemDTO.setStock(cartItem.getProduct().getStock());
                        cartItemDTO.setValid(cartItem.getProduct().getPublishStatus() == 1);
                    }
                    
                    // SKU信息
                    if (cartItem.getSku() != null) {
                        cartItemDTO.setSkuId(cartItem.getSku().getId());
                        cartItemDTO.setSkuSpecs(cartItem.getSku().getSpecs() != null ? 
                            cartItem.getSku().getSpecs().toString() : null);
                        cartItemDTO.setPrice(cartItem.getSku().getPrice());
                        cartItemDTO.setStock(cartItem.getSku().getStock());
                    }
                    
                    // 计算小计
                    cartItemDTO.setSubtotal(cartItem.getSubtotal());
                    
                    return cartItemDTO;
                }
        ).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "cartCount", key = "#userId", unless = "#result == null")
    public Integer getCartCount(String userId) {
        return cartItemRepository.findByUserId(userId).size();
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public CartItemDTO addToCart(String userId, AddCartDTO addCartDTO) {
        // 检查是否已存在相同 SKU 的购物车项
        CartItem existingItem = cartItemRepository.findByUserIdAndSkuId(userId, addCartDTO.getSkuId()).orElse(null);
        
        if (existingItem != null) {
            // 如果已存在，增加数量
            existingItem.setQuantity(existingItem.getQuantity() + addCartDTO.getQuantity());
            cartItemRepository.save(existingItem);
            return convertToDTO(existingItem);
        } else {
            // 创建新的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在")));
            cartItem.setProduct(productRepository.findById(addCartDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在")));
            cartItem.setSku(productSkuRepository.findById(addCartDTO.getSkuId())
                .orElseThrow(() -> new RuntimeException("SKU不存在")));
            cartItem.setQuantity(addCartDTO.getQuantity());
            cartItem.setChecked(1);
            cartItemRepository.save(cartItem);
            return convertToDTO(cartItem);
        }
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public CartItemDTO updateQuantity(String userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        if (quantity <= 0) {
            throw new RuntimeException("数量必须大于0");
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return convertToDTO(cartItem);
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public boolean checkItem(String userId, Long cartItemId, Integer checked) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        cartItem.setChecked(checked);
        cartItemRepository.save(cartItem);
        return true;
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public boolean checkAll(String userId, Integer checked) {
        cartItemRepository.updateCheckedStatus(userId, checked);
        return true;
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public boolean deleteItem(String userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        cartItemRepository.deleteById(cartItemId);
        return true;
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public boolean batchDelete(String userId, List<Long> cartItemIds) {
        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
            if (cartItem != null && cartItem.getUser().getId().equals(userId)) {
                cartItemRepository.deleteById(cartItemId);
            }
        }
        return true;
    }

    @Override
    @CacheEvict(value = {"cartList", "cartCount"}, key = "#userId")
    public boolean clearCart(String userId) {
        cartItemRepository.clearByUserId(userId);
        return true;
    }

    @Override
    public CartStatisticsDTO getStatistics(String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        CartStatisticsDTO statistics = new CartStatisticsDTO();
//        statistics.setTotalItems(cartItems.size());
//        statistics.setTotalQuantity(cartItems.stream()
//            .mapToInt(CartItem::getQuantity)
//            .sum());
        
        // 计算选中商品的总价
        java.math.BigDecimal totalAmount = cartItems.stream()
            .filter(item -> item.getChecked() == 1)
            .map(CartItem::getSubtotal)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        statistics.setTotalAmount(totalAmount);
        
        // 统计选中商品数量
//        statistics.setCheckedCount(cartItems.stream()
//            .filter(item -> item.getChecked() == 1)
//            .mapToInt(CartItem::getQuantity)
//            .sum());
        
        return statistics;
    }
    
    /**
     * 转换 CartItem 到 CartItemDTO
     */
    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        // 基本信息
        dto.setId(cartItem.getId());
        dto.setUserId(cartItem.getUser().getId());
        dto.setChecked(cartItem.getChecked());
        dto.setQuantity(cartItem.getQuantity());
        
        // 商品信息
        if (cartItem.getProduct() != null) {
            dto.setProductId(cartItem.getProduct().getId());
            dto.setProductName(cartItem.getProduct().getName());
            dto.setProductImage(cartItem.getProduct().getImage());
            dto.setPrice(cartItem.getProduct().getPrice());
            dto.setStock(cartItem.getProduct().getStock());
            dto.setValid(cartItem.getProduct().getPublishStatus() == 1);
        }
        
        // SKU信息
        if (cartItem.getSku() != null) {
            dto.setSkuId(cartItem.getSku().getId());
            dto.setSkuSpecs(cartItem.getSku().getSpecs() != null ? 
                cartItem.getSku().getSpecs().toString() : null);
            dto.setPrice(cartItem.getSku().getPrice());
            dto.setStock(cartItem.getSku().getStock());
        }
        
        // 计算小计
        dto.setSubtotal(cartItem.getSubtotal());
        
        return dto;
    }
}
