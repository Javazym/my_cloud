package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.merchant.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 商家账户Repository
 */
@Repository
public interface MerchantAccountRepository extends JpaRepository<MerchantAccount, Long> {

    /**
     * 根据商家ID查询账户
     */
    Optional<MerchantAccount> findByMerchantId(Long merchantId);

    /**
     * 增加收入
     */
    @Modifying
    @Query("UPDATE MerchantAccount m SET m.totalIncome = m.totalIncome + :amount, m.withdrawable = m.withdrawable + :amount WHERE m.merchant.id = :merchantId")
    void addIncome(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount);

    /**
     * 扣减可提现金额
     */
    @Modifying
    @Query("UPDATE MerchantAccount m SET m.withdrawable = m.withdrawable - :amount, m.pendingWithdraw = m.pendingWithdraw + :amount WHERE m.merchant.id = :merchantId AND m.withdrawable >= :amount")
    int applyWithdraw(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount);

    /**
     * 审核通过提现
     */
    @Modifying
    @Query("UPDATE MerchantAccount m SET m.pendingWithdraw = m.pendingWithdraw - :amount, m.withdrawn = m.withdrawn + :amount WHERE m.merchant.id = :merchantId")
    int approveWithdraw(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount);

    /**
     * 拒绝提现
     */
    @Modifying
    @Query("UPDATE MerchantAccount m SET m.pendingWithdraw = m.pendingWithdraw - :amount, m.withdrawable = m.withdrawable + :amount WHERE m.merchant.id = :merchantId")
    int rejectWithdraw(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount);
}
