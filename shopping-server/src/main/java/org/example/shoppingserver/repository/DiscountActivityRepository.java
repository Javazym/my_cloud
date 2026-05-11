package org.example.shoppingserver.repository;

import org.example.shoppingserver.model.entity.marketing.DiscountActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 满减活动Repository
 */
@Repository
public interface DiscountActivityRepository extends JpaRepository<DiscountActivity, Long>, JpaSpecificationExecutor<DiscountActivity> {

    /**
     * 根据商家ID查询
     */
    Page<DiscountActivity> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态查询
     */
    Page<DiscountActivity> findByMerchantIdAndStatus(Long merchantId, Integer status, Pageable pageable);

    /**
     * 查询进行中的满减活动
     */
    List<DiscountActivity> findByStatusAndStartTimeBeforeAndEndTimeAfter(Integer status, java.time.LocalDateTime now, java.time.LocalDateTime now2);

    /**
     * 统计商家的满减活动数量
     */
    long countByMerchantId(Long merchantId);
}
