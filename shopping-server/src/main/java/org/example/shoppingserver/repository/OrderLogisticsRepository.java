package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.OrderLogistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 物流信息Repository
 */
@Repository
public interface OrderLogisticsRepository extends JpaRepository<OrderLogistics, Long> {

    /**
     * 根据订单ID查询物流信息
     */
    Optional<OrderLogistics> findByOrderId(Long orderId);

    /**
     * 根据物流单号查询物流信息
     */
    Optional<OrderLogistics> findByTrackingNumber(String trackingNumber);
}
