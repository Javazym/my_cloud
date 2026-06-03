package org.example.shoppingserver.repository;

import org.example.shoppingserver.model.entity.marketing.SeckillActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 秒杀活动Repository
 */
@Repository
public interface SeckillActivityRepository extends JpaRepository<SeckillActivity, Long>, JpaSpecificationExecutor<SeckillActivity> {

    /**
     * 根据商家ID查询
     */
    Page<SeckillActivity> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态查询
     */
    Page<SeckillActivity> findByMerchantIdAndStatus(Long merchantId, Integer status, Pageable pageable);

    /**
     * 查询进行中的秒杀活动
     */
    List<SeckillActivity> findByStatusAndStartTimeBeforeAndEndTimeAfter(Integer status, java.time.LocalDateTime now, java.time.LocalDateTime now2);

    /**
     * 根据状态列表查询活动
     */
    List<SeckillActivity> findByStatusIn(List<Integer> statuses);

    /**
     * 根据商品ID查询
     */
    List<SeckillActivity> findByProduct_Id(Long productId);

    /**
     * 根据商品ID和状态列表查询（用于检查商品是否已有进行中的活动）
     */
    List<SeckillActivity> findByProduct_IdAndStatusIn(Long productId, java.util.List<Integer> statuses);

    /**
     * 统计商家的秒杀活动数量
     */
    long countByMerchantId(Long merchantId);
}
