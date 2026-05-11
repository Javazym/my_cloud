package org.example.shoppingserver.repository;

import org.example.shoppingserver.model.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车Repository
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 根据用户ID查询购物车列表
     */
    List<CartItem> findByUserId(String userId);

    /**
     * 根据用户ID查询选中的购物车商品
     */
    @Query("SELECT c FROM CartItem c WHERE c.user.id = :userId AND c.checked = 1")
    List<CartItem> findCheckedItems(@Param("userId") String userId);

    /**
     * 根据用户ID和商品ID查询购物车商品
     */
    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    /**
     * 根据用户ID和SKU ID查询购物车商品
     */
    Optional<CartItem> findByUserIdAndSkuId(String userId, Long skuId);

    /**
     * 统计用户购物车商品数量
     */
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user.id = :userId")
    Integer countByUserId(@Param("userId") String userId);

    /**
     * 统计用户购物车选中商品数量
     */
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.id = :userId AND c.checked = 1")
    Integer countCheckedByUserId(@Param("userId") String userId);

    /**
     * 清空用户购物车
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void clearByUserId(@Param("userId") String userId);

    /**
     * 清空用户选中的购物车商品
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.checked = 1")
    void clearCheckedByUserId(@Param("userId") String userId);

    /**
     * 全选/取消全选
     */
    @Modifying
    @Query("UPDATE CartItem c SET c.checked = :checked WHERE c.user.id = :userId")
    void updateCheckedStatus(@Param("userId") String userId, @Param("checked") Integer checked);
}
