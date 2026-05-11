package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.common.AuditStatus;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商家Repository
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 根据用户ID查询商家
     */
    Optional<Merchant> findByUserId(String userId);

    /**
     * 根据店铺名称查询商家
     */
    Optional<Merchant> findByStoreName(String storeName);

    /**
     * 根据状态查询商家列表
     */
    Page<Merchant> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据审核状态查询商家列表
     */
    Page<Merchant> findByAuditStatus(AuditStatus auditStatus, Pageable pageable);

    /**
     * 查询待审核商家列表
     */
    @Query("SELECT m FROM Merchant m WHERE m.auditStatus = 0")
    List<Merchant> findPendingAuditMerchants();

    /**
     * 统计商家数量
     */
    long countByStatus(Integer status);

    /**
     * 统计待审核商家数量
     */
    long countByAuditStatus(AuditStatus auditStatus);

    /**
     * 根据主营类目查询商家
     */
    Page<Merchant> findByCategory(String category, Pageable pageable);

    /**
     * 查询热门商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.status = 1 ORDER BY m.followers DESC")
    List<Merchant> findPopularMerchants(Pageable pageable);
}
