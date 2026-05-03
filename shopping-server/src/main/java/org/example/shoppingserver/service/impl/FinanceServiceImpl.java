package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.entity.MerchantAccount;
import org.example.shoppingserver.model.entity.WithdrawRecord;
import org.example.shoppingserver.model.entity.Merchant;
import org.example.shoppingserver.repository.MerchantAccountRepository;
import org.example.shoppingserver.repository.WithdrawRecordRepository;
import org.example.shoppingserver.service.FinanceService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final MerchantAccountRepository merchantAccountRepository;
    private final WithdrawRecordRepository withdrawRecordRepository;

    // ====================== 1. 获取商家账户 ======================
    @Override
    @Cacheable(value = "merchantAccount", key = "#merchantId", unless = "#result == null")
    public MerchantAccountDTO getAccount(Long merchantId) {
        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        MerchantAccountDTO dto = new MerchantAccountDTO();
        dto.setMerchantId(merchantId);
        dto.setTotalIncome(account.getTotalIncome());
        dto.setWithdrawable(account.getWithdrawable());
        dto.setWithdrawn(account.getWithdrawn());
        dto.setPendingWithdraw(account.getPendingWithdraw());
        return dto;
    }

    // ====================== 2. 获取财务统计 ======================
    @Override
    public FinanceDataDTO getFinanceData(Long merchantId, String startDate, String endDate) {
        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        FinanceDataDTO dto = new FinanceDataDTO();
        dto.setTotalIncome(account.getTotalIncome());
        dto.setMonthIncome(BigDecimal.ZERO);
        dto.setTodayIncome(BigDecimal.ZERO);
        dto.setOrderCount(0L);
        dto.setMonthOrderCount(0L);
        dto.setAverageOrderAmount(BigDecimal.ZERO);
        dto.setRefundAmount(BigDecimal.ZERO);
        return dto;
    }

    // ====================== 3. 申请提现 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"merchantAccount", "withdrawRecords"}, allEntries = true)
    public boolean applyWithdraw(Long merchantId, WithdrawDTO withdrawDTO) {
        // 1. 获取账户
        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));

        BigDecimal amount = withdrawDTO.getAmount();
        BigDecimal withdrawable = account.getWithdrawable();

        // 2. 校验金额
        if (amount.compareTo(withdrawable) > 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // 3. 扣除可提现金额
        int rows = merchantAccountRepository.applyWithdraw(merchantId, amount);
        if (rows <= 0) return false;

        // 4. 创建提现记录
        WithdrawRecord record = new WithdrawRecord();
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);

        record.setMerchant(merchant);
        record.setAmount(amount);
        record.setFee(BigDecimal.ZERO);
        record.setActualAmount(amount);
        record.setBankName(withdrawDTO.getBankName());
        record.setBankAccount(withdrawDTO.getBankAccount());
        record.setAccountName(withdrawDTO.getAccountName());
        record.setStatus(0);
        record.setApplyTime(LocalDateTime.now());

        withdrawRecordRepository.save(record);
        return true;
    }

    // ====================== 4. 获取提现记录 ======================
    @Override
    public Page<WithdrawRecordDTO> getWithdrawRecords(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<WithdrawRecord> page;

        if (status == null) {
            page = withdrawRecordRepository.findByMerchantId(merchantId, pageable);
        } else {
            page = withdrawRecordRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
        }

        return page.map(this::convertToDTO);
    }

    // ====================== 工具：转DTO ======================
    private WithdrawRecordDTO convertToDTO(WithdrawRecord record) {
        WithdrawRecordDTO dto = new WithdrawRecordDTO();
        dto.setId(record.getId());
        dto.setAmount(record.getAmount());
        dto.setFee(record.getFee());
        dto.setActualAmount(record.getActualAmount());
        dto.setBankName(record.getBankName());
        dto.setBankAccount(record.getBankAccount());
        dto.setStatus(record.getStatus());
        dto.setStatusText(getStatusText(record.getStatus()));
        dto.setApplyTime(record.getApplyTime());
        dto.setProcessTime(record.getProcessTime());
        return dto;
    }

    private String getStatusText(Integer status) {
        if (status == 0) return "待审核";
        if (status == 1) return "审核通过";
        if (status == 2) return "已打款";
        if (status == 3) return "已拒绝";
        return "未知";
    }
}