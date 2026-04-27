package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.AuditStatus;
import org.example.shoppingserver.model.entity.MerchantApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商家入驻申请Repository
 */
@Repository
public interface MerchantApplicationRepository extends JpaRepository<MerchantApplication, Long>,
        JpaSpecificationExecutor<MerchantApplication> {

    /**
     * 根据用户ID查询入驻申请
     */
    MerchantApplication findByUserId(String userId);

    /**
     * 根据状态查询入驻申请列表
     */
    Page<MerchantApplication> findByStatus(AuditStatus status, Pageable pageable);

    /**
     * 查询用户的最新申请
     */
    @Query("SELECT m FROM MerchantApplication m WHERE m.user.id = :userId ORDER BY m.createdAt DESC LIMIT 1")
    Optional<MerchantApplication> findLatestByUserId(@Param("userId") String userId);

    /**
     * 统计待审核申请数量
     */
    long countByStatus(AuditStatus status);

    /**
     * 检查用户是否有待处理的申请
     */
    boolean existsByUserIdAndStatus(String userId, AuditStatus status);
}
