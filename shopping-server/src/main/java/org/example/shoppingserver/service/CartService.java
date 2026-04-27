package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.AddCartDTO;
import org.example.shoppingserver.model.dto.CartItemDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车Service接口
 */
public interface CartService {

    /**
     * 获取购物车列表
     *
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    List<CartItemDTO> getCartList(String userId);

    /**
     * 获取选中的购物车商品
     *
     * @param userId 用户ID
     * @return 选中的购物车商品列表
     */
    List<CartItemDTO> getCheckedItems(String userId);

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品数量
     */
    Integer getCartCount(String userId);

    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param addCartDTO 添加DTO
     * @return 购物车商品
     */
    CartItemDTO addToCart(String userId, AddCartDTO addCartDTO);

    /**
     * 更新购物车商品数量
     *
     * @param userId     用户ID
     * @param cartItemId 购物车商品ID
     * @param quantity   数量
     * @return 购物车商品
     */
    CartItemDTO updateQuantity(String userId, Long cartItemId, Integer quantity);

    /**
     * 选中/取消选中购物车商品
     *
     * @param userId     用户ID
     * @param cartItemId 购物车商品ID
     * @param checked    是否选中
     * @return 是否成功
     */
    boolean checkItem(String userId, Long cartItemId, Integer checked);

    /**
     * 全选/取消全选
     *
     * @param userId  用户ID
     * @param checked 是否选中
     * @return 是否成功
     */
    boolean checkAll(String userId, Integer checked);

    /**
     * 删除购物车商品
     *
     * @param userId     用户ID
     * @param cartItemId 购物车商品ID
     * @return 是否成功
     */
    boolean deleteItem(String userId, Long cartItemId);

    /**
     * 批量删除购物车商品
     *
     * @param userId      用户ID
     * @param cartItemIds 购物车商品ID列表
     * @return 是否成功
     */
    boolean batchDelete(String userId, List<Long> cartItemIds);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearCart(String userId);

    /**
     * 获取购物车统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    CartStatisticsDTO getStatistics(String userId);

    /**
     * 购物车统计信息DTO
     */
    class CartStatisticsDTO {
        private Integer totalCount;
        private Integer selectedCount;
        private BigDecimal totalAmount;
        private BigDecimal selectedAmount;

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        public Integer getSelectedCount() { return selectedCount; }
        public void setSelectedCount(Integer selectedCount) { this.selectedCount = selectedCount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getSelectedAmount() { return selectedAmount; }
        public void setSelectedAmount(BigDecimal selectedAmount) { this.selectedAmount = selectedAmount; }
    }
}
