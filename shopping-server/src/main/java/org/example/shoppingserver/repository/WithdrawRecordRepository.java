package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 提现记录Repository
 */
@Repository
public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Long> {

    /**
     * 根据商家ID查询提现记录
     */
    Page<WithdrawRecord> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 根据商家ID和状态查询提现记录
     */
    Page<WithdrawRecord> findByMerchantIdAndStatus(Long merchantId, Integer status, Pageable pageable);

    /**
     * 根据状态查询提现记录
     */
    List<WithdrawRecord> findByStatus(Integer status);

    /**
     * 根据状态分页查询提现记录
     */
    Page<WithdrawRecord> findByStatus(Integer status, Pageable pageable);

    /**
     * 统计商家提现记录数量
     */
    long countByMerchantId(Long merchantId);

    /**
     * 统计商家待审核提现数量
     */
    long countByMerchantIdAndStatus(Long merchantId, Integer status);

    /**
     * 统计指定状态的提现记录数量
     */
    long countByStatus(Integer status);

    /**
     * 查询商家的最新提现记录
     */
    Optional<WithdrawRecord> findTopByMerchantIdOrderByCreatedAtDesc(Long merchantId);
}
