package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.model.dto.cart.AddCartDTO;
import org.example.shoppingserver.model.vo.cart.CartItemVO;
import org.example.shoppingserver.model.vo.cart.CartStatisticsVO;
import org.example.shoppingserver.model.entity.cart.CartItem;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.product.ProductSku;
import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.repository.CartItemRepository;
import org.example.shoppingserver.repository.ProductRepository;
import org.example.shoppingserver.repository.ProductSkuRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.CartService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductRepository productRepository;

    @Override
    @Cacheable(value = "cartList", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<CartItemVO> getCartList(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        log.debug("获取购物车列表 - 用户ID: {}", userId);
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "checkedItems", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<CartItemVO> getCheckedItems(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        log.debug("获取选中商品 - 用户ID: {}", userId);
        return cartItemRepository.findCheckedItems(userId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "cartCount", key = "#userId", unless = "#result == null")
    public Integer getCartCount(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        Integer count = cartItemRepository.countByUserId(userId);
        return count != null ? count : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public CartItemVO addToCart(String userId, AddCartDTO addCartDTO) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (addCartDTO == null) {
            throw new IllegalArgumentException("添加购物车信息不能为空");
        }

        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));

        // 验证商品是否存在
        Product product = productRepository.findById(addCartDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + addCartDTO.getProductId()));

        // 验证SKU是否存在
        ProductSku sku = productSkuRepository.findById(addCartDTO.getSkuId())
                .orElseThrow(() -> new RuntimeException("SKU不存在，ID: " + addCartDTO.getSkuId()));

        // 验证库存
        if (sku.getStock() < addCartDTO.getQuantity()) {
            throw new RuntimeException("库存不足，当前库存: " + sku.getStock());
        }

        // 检查是否已存在相同 SKU 的购物车项
        CartItem existingItem = cartItemRepository.findByUserIdAndSkuId(userId, addCartDTO.getSkuId()).orElse(null);
        
        if (existingItem != null) {
            // 如果已存在，增加数量
            int newQuantity = existingItem.getQuantity() + addCartDTO.getQuantity();
            
            // 验证总数量不超过库存
            if (sku.getStock() < newQuantity) {
                throw new RuntimeException("库存不足，当前库存: " + sku.getStock() + "，购物车已有: " + existingItem.getQuantity());
            }
            
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
            log.info("更新购物车商品数量 - 用户ID: {}, SKU ID: {}, 新数量: {}", userId, addCartDTO.getSkuId(), newQuantity);
            return convertToVO(existingItem);
        } else {
            // 创建新的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setSku(sku);
            cartItem.setQuantity(addCartDTO.getQuantity());
            cartItem.setChecked(1);
            cartItemRepository.save(cartItem);
            log.info("添加商品到购物车 - 用户ID: {}, 商品ID: {}, SKU ID: {}, 数量: {}", 
                    userId, addCartDTO.getProductId(), addCartDTO.getSkuId(), addCartDTO.getQuantity());
            return convertToVO(cartItem);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public CartItemVO updateQuantity(String userId, Long cartItemId, Integer quantity) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (cartItemId == null || cartItemId <= 0) {
            throw new IllegalArgumentException("购物车商品ID无效");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车商品不存在，ID: " + cartItemId));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        // 验证库存
        ProductSku sku = cartItem.getSku();
        if (sku != null && sku.getStock() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + sku.getStock());
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        log.info("更新购物车商品数量 - 用户ID: {}, 购物车ID: {}, 新数量: {}", userId, cartItemId, quantity);
        return convertToVO(cartItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public boolean checkItem(String userId, Long cartItemId, Integer checked) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (cartItemId == null || cartItemId <= 0) {
            throw new IllegalArgumentException("购物车商品ID无效");
        }
        
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new IllegalArgumentException("选中状态无效，必须为0或1");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车商品不存在，ID: " + cartItemId));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        cartItem.setChecked(checked);
        cartItemRepository.save(cartItem);
        
        log.info("{}购物车商品 - 用户ID: {}, 购物车ID: {}", checked == 1 ? "选中" : "取消选中", userId, cartItemId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public boolean checkAll(String userId, Integer checked) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (checked == null || (checked != 0 && checked != 1)) {
            throw new IllegalArgumentException("选中状态无效，必须为0或1");
        }

        cartItemRepository.updateCheckedStatus(userId, checked);
        log.info("{}所有购物车商品 - 用户ID: {}", checked == 1 ? "全选" : "取消全选", userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public boolean deleteItem(String userId, Long cartItemId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (cartItemId == null || cartItemId <= 0) {
            throw new IllegalArgumentException("购物车商品ID无效");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车商品不存在，ID: " + cartItemId));
        
        // 验证是否是当前用户的购物车
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此购物车商品");
        }
        
        cartItemRepository.deleteById(cartItemId);
        log.info("删除购物车商品 - 用户ID: {}, 购物车ID: {}", userId, cartItemId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public boolean batchDelete(String userId, List<Long> cartItemIds) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("购物车商品ID列表不能为空");
        }

        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
            if (cartItem != null && cartItem.getUser().getId().equals(userId)) {
                cartItemRepository.deleteById(cartItemId);
            }
        }
        
        log.info("批量删除购物车商品 - 用户ID: {}, 删除数量: {}", userId, cartItemIds.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"cartList", "cartCount", "checkedItems"}, allEntries = true)
    public boolean clearCart(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        cartItemRepository.clearByUserId(userId);
        log.info("清空购物车 - 用户ID: {}", userId);
        return true;
    }

    @Override
    @Cacheable(value = "cartStatistics", key = "#userId", unless = "#result == null")
    public CartStatisticsVO getStatistics(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        CartStatisticsVO statistics = new CartStatisticsVO();
        
        // 总商品数量（不同SKU数）
        statistics.setTotalCount(cartItems.size());
        
        // 选中商品数量
        long selectedCount = cartItems.stream()
                .filter(item -> item.getChecked() == 1)
                .count();
        statistics.setSelectedCount((int) selectedCount);
        
        // 总金额（所有商品）
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalAmount(totalAmount);
        
        // 选中金额
        BigDecimal selectedAmount = cartItems.stream()
                .filter(item -> item.getChecked() == 1)
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setSelectedAmount(selectedAmount);
        
        log.debug("获取购物车统计 - 用户ID: {}, 总数: {}, 选中: {}, 总金额: {}, 选中金额: {}",
                userId, statistics.getTotalCount(), statistics.getSelectedCount(), 
                statistics.getTotalAmount(), statistics.getSelectedAmount());
        
        return statistics;
    }
    
    /**
     * 转换 CartItem 到 CartItemVO
     */
    private CartItemVO convertToVO(CartItem cartItem) {
        CartItemVO vo = new CartItemVO();
        
        // 基本信息
        vo.setId(cartItem.getId());
        vo.setUserId(cartItem.getUser().getId());
        vo.setChecked(cartItem.getChecked());
        vo.setQuantity(cartItem.getQuantity());
        
        // 商品信息
        if (cartItem.getProduct() != null) {
            vo.setProductId(cartItem.getProduct().getId());
            vo.setProductName(cartItem.getProduct().getName());
            vo.setProductImage(cartItem.getProduct().getImage());
            vo.setValid(cartItem.getProduct().getPublishStatus() == 1);
        }
        
        // SKU信息（优先使用SKU的价格和库存）
        if (cartItem.getSku() != null) {
            vo.setSkuId(cartItem.getSku().getId());
            vo.setSkuSpecs(cartItem.getSku().getSpecs() != null ? 
                cartItem.getSku().getSpecs().toString() : null);
            vo.setPrice(cartItem.getSku().getPrice());
            vo.setStock(cartItem.getSku().getStock());
        } else if (cartItem.getProduct() != null) {
            // 如果没有SKU，使用商品信息
            vo.setPrice(cartItem.getProduct().getPrice());
            vo.setStock(cartItem.getProduct().getStock());
        }
        
        // 计算小计
        vo.setSubtotal(cartItem.getSubtotal());
        
        return vo;
    }
}
