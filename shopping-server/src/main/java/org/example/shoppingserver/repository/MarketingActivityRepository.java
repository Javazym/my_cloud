package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.MarketingActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销活动Repository
 */
@Repository
public interface MarketingActivityRepository extends JpaRepository<MarketingActivity, Long> {

    /**
     * 根据商家ID查询营销活动
     */
    List<MarketingActivity> findByMerchantId(Long merchantId);

    /**
     * 根据类型查询营销活动
     */
    List<MarketingActivity> findByType(String type);

    /**
     * 根据状态查询营销活动
     */
    List<MarketingActivity> findByStatus(Integer status);

    /**
     * 查询进行中的活动
     */
    @Query("SELECT m FROM MarketingActivity m WHERE m.status = 1 AND m.startTime <= :now AND m.endTime >= :now")
    List<MarketingActivity> findActiveActivities(@Param("now") LocalDateTime now);

    /**
     * 查询商家的进行中活动
     */
    @Query("SELECT m FROM MarketingActivity m WHERE m.merchant.id = :merchantId AND m.status = 1 AND m.startTime <= :now AND m.endTime >= :now")
    List<MarketingActivity> findActiveByMerchantId(@Param("merchantId") Long merchantId, @Param("now") LocalDateTime now);

    /**
     * 根据状态分页查询
     */
    Page<MarketingActivity> findByStatus(Integer status, Pageable pageable);

    /**
     * 更新活动状态
     */
    @Query("SELECT m FROM MarketingActivity m WHERE m.startTime <= :now AND m.endTime >= :now AND m.status = 0")
    List<MarketingActivity> findActivitiesToStart(@Param("now") LocalDateTime now);

    /**
     * 查询过期的活动
     */
    @Query("SELECT m FROM MarketingActivity m WHERE m.endTime < :now AND m.status = 1")
    List<MarketingActivity> findExpiredActivities(@Param("now") LocalDateTime now);
}
