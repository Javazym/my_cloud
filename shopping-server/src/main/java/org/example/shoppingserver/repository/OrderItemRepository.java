package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单商品Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单ID查询订单商品列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据订单号查询订单商品列表
     */
    List<OrderItem> findByOrderNo(String orderNo);

    /**
     * 根据商品ID查询订单商品列表
     */
    List<OrderItem> findByProductId(Long productId);

    /**
     * 查询订单中的商品是否已评价
     */
    @Query("SELECT o FROM OrderItem o WHERE o.order.id = :orderId AND o.reviewStatus = 1")
    List<OrderItem> findReviewedItems(@Param("orderId") Long orderId);

    /**
     * 批量更新评价状态
     */
    @Modifying
    @Query("UPDATE OrderItem o SET o.reviewStatus = 1 WHERE o.id IN :ids")
    void batchUpdateReviewStatus(@Param("ids") List<Long> ids);

    /**
     * 统计订单商品数量
     */
    long countByOrderId(Long orderId);

    /**
     * 统计商品销量
     */
    @Query("SELECT SUM(o.quantity) FROM OrderItem o WHERE o.product.id = :productId")
    Long sumSalesByProductId(@Param("productId") Long productId);
}
