package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.cart.AddCartDTO;
import org.example.shoppingserver.model.vo.cart.CartItemVO;
import org.example.shoppingserver.model.vo.cart.CartStatisticsVO;

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
    List<CartItemVO> getCartList(String userId);

    /**
     * 获取选中的购物车商品
     *
     * @param userId 用户ID
     * @return 选中的购物车商品列表
     */
    List<CartItemVO> getCheckedItems(String userId);

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
    CartItemVO addToCart(String userId, AddCartDTO addCartDTO);

    /**
     * 更新购物车商品数量
     *
     * @param userId     用户ID
     * @param cartItemId 购物车商品ID
     * @param quantity   数量
     * @return 购物车商品
     */
    CartItemVO updateQuantity(String userId, Long cartItemId, Integer quantity);

    /**
     * 选中/取消选中购物车商品
     *
     * @param userId     用户ID
     * @param cartItemId 购物车商品ID
     * @param checked    是否选中 (0-否, 1-是)
     * @return 是否成功
     */
    boolean checkItem(String userId, Long cartItemId, Integer checked);

    /**
     * 全选/取消全选
     *
     * @param userId  用户ID
     * @param checked 是否选中 (0-否, 1-是)
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
    CartStatisticsVO getStatistics(String userId);
}
