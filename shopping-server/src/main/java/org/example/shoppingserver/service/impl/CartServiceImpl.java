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
    public List<CartItemDTO> getCartList(String userId) {
        return cartItemRepository.findByUserId(userId).stream().map(
                cartItem -> {
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    cartItemDTO.setId(cartItem.getId());
                    cartItemDTO.setUserId(cartItem.getUser().getId());
                    cartItemDTO.setProductId(cartItem.getProduct().getId());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    cartItemDTO.setChecked(cartItem.getChecked());
                    return cartItemDTO;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<CartItemDTO> getCheckedItems(String userId) {

        return List.of();
    }

    @Override
    public Integer getCartCount(String userId) {
        return cartItemRepository.findByUserId(userId).size();
    }

    @Override
    public CartItemDTO addToCart(String userId, AddCartDTO addCartDTO) {
        CartItem cartItem = new CartItem();
        cartItem.setUser(userRepository.findById(userId).get());
        cartItem.setSku(productSkuRepository.findById(addCartDTO.getSkuId()).get());
        cartItem.setProduct(productRepository.findById(addCartDTO.getProductId()).get());
        cartItem.setQuantity(addCartDTO.getQuantity());
        cartItemRepository.save(cartItem);
        return null;
    }

    @Override
    public CartItemDTO updateQuantity(String userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return null;
    }

    @Override
    public boolean checkItem(String userId, Long cartItemId, Integer checked) {
        cartItemRepository.updateCheckedStatus(userId, checked);
        return false;
    }

    @Override
    public boolean checkAll(String userId, Integer checked) {
        cartItemRepository.updateCheckedStatus(userId, checked);
        return false;
    }

    @Override
    public boolean deleteItem(String userId, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return false;
    }

    @Override
    public boolean batchDelete(String userId, List<Long> cartItemIds) {
        cartItemRepository.deleteAllById(cartItemIds);
        return false;
    }

    @Override
    public boolean clearCart(String userId) {
        cartItemRepository.clearByUserId(userId);
        return false;
    }

    @Override
    public CartStatisticsDTO getStatistics(String userId) {
        return null;
    }
}
