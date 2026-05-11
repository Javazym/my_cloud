package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.model.vo.finance.MerchantAccountVO;
import org.example.shoppingserver.model.vo.finance.FinanceDataVO;
import org.example.shoppingserver.model.dto.finance.WithdrawDTO;
import org.example.shoppingserver.model.vo.finance.WithdrawRecordVO;
import org.example.shoppingserver.model.entity.merchant.MerchantAccount;
import org.example.shoppingserver.model.entity.finance.WithdrawRecord;
import org.example.shoppingserver.model.entity.merchant.Merchant;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final MerchantAccountRepository merchantAccountRepository;
    private final WithdrawRecordRepository withdrawRecordRepository;

    // ====================== 1. 获取商家账户 ======================
    @Override
    @Cacheable(value = "merchantAccount", key = "#merchantId", unless = "#result == null")
    public MerchantAccountVO getAccount(Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空或无效");
        }

        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在，商家ID: " + merchantId));

        MerchantAccountVO vo = new MerchantAccountVO();
        vo.setMerchantId(merchantId);
        vo.setBalance(account.getWithdrawable());
        vo.setFrozenAmount(account.getPendingWithdraw());
        vo.setTotalIncome(account.getTotalIncome());
        vo.setTotalWithdraw(account.getWithdrawn());
        return vo;
    }

    // ====================== 2. 获取财务统计 ======================
    @Override
    public FinanceDataVO getFinanceData(Long merchantId, String startDate, String endDate) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空或无效");
        }

        // 验证日期格式
        if (startDate != null && !startDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("开始日期格式错误，应为yyyy-MM-dd");
        }
        if (endDate != null && !endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("结束日期格式错误，应为yyyy-MM-dd");
        }

        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在，商家ID: " + merchantId));

        FinanceDataVO vo = new FinanceDataVO();
        vo.setTotalIncome(account.getTotalIncome());
        
        // TODO: 这里应该根据实际订单数据计算月度、今日收入等统计信息
        // 目前返回默认值，实际项目中需要连接订单表进行统计
        vo.setTotalExpense(BigDecimal.ZERO);
        vo.setNetProfit(account.getTotalIncome());
        vo.setOrderCount(0L);
        vo.setRefundAmount(BigDecimal.ZERO);
        
        log.info("获取商家财务数据 - 商家ID: {}, 总收入: {}", merchantId, account.getTotalIncome());
        return vo;
    }

    // ====================== 3. 申请提现 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"merchantAccount", "withdrawRecords"}, allEntries = true)
    public boolean applyWithdraw(Long merchantId, WithdrawDTO withdrawDTO) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空或无效");
        }
        
        if (withdrawDTO == null) {
            throw new IllegalArgumentException("提现信息不能为空");
        }

        // 1. 获取账户
        MerchantAccount account = merchantAccountRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("账户不存在，商家ID: " + merchantId));

        BigDecimal amount = withdrawDTO.getAmount();
        BigDecimal withdrawable = account.getWithdrawable();

        // 2. 校验金额
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提现金额必须大于0");
        }
        
        if (amount.compareTo(withdrawable) > 0) {
            throw new IllegalArgumentException("提现金额超过可提现余额，当前可提现: " + withdrawable);
        }

        // 3. 扣除可提现金额
        int rows = merchantAccountRepository.applyWithdraw(merchantId, amount);
        if (rows <= 0) {
            throw new RuntimeException("提现操作失败，请稍后重试");
        }

        // 4. 创建提现记录
        WithdrawRecord record = new WithdrawRecord();
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);

        record.setMerchant(merchant);
        record.setAmount(amount);
        record.setFee(BigDecimal.ZERO); // 可以根据业务规则计算手续费
        record.setActualAmount(amount.subtract(record.getFee())); // 实际到账金额
        record.setBankName(withdrawDTO.getBankName());
        record.setBankAccount(withdrawDTO.getBankAccount());
        record.setAccountName(withdrawDTO.getAccountName());
        record.setStatus(0); // 待审核状态
        record.setApplyTime(LocalDateTime.now());

        withdrawRecordRepository.save(record);
        
        log.info("商家申请提现成功 - 商家ID: {}, 金额: {}", merchantId, amount);
        return true;
    }

    // ====================== 4. 获取提现记录 ======================
    @Override
    @Cacheable(value = "withdrawRecords", key = "#merchantId + '_' + (#status != null ? #status : 'all') + '_' + #pageNum + '_' + #pageSize", unless = "#result == null || #result.isEmpty()")
    public Page<WithdrawRecordVO> getWithdrawRecords(Long merchantId, Integer status, int pageNum, int pageSize) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空或无效");
        }
        
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<WithdrawRecord> page;

        if (status == null) {
            page = withdrawRecordRepository.findByMerchantId(merchantId, pageable);
        } else {
            page = withdrawRecordRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
        }

        return page.map(this::convertToVO);
    }

    // ====================== 5. 审核提现申请 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"merchantAccount", "withdrawRecords"}, allEntries = true)
    public boolean auditWithdraw(Long recordId, boolean approved, String reason) {
        if (recordId == null || recordId <= 0) {
            throw new IllegalArgumentException("提现记录ID不能为空或无效");
        }

        Optional<WithdrawRecord> optionalRecord = withdrawRecordRepository.findById(recordId);
        if (optionalRecord.isEmpty()) {
            throw new RuntimeException("提现记录不存在，记录ID: " + recordId);
        }

        WithdrawRecord record = optionalRecord.get();
        
        // 只有待审核状态的记录才能被审核
        if (record.getStatus() != 0) {
            throw new RuntimeException("该提现记录已被处理，无法重复审核");
        }

        Long merchantId = record.getMerchant().getId();
        BigDecimal amount = record.getAmount();

        if (approved) {
            // 审核通过
            record.approve();
            
            // 更新商家账户：减少待提现，增加已提现
            int rows = merchantAccountRepository.approveWithdraw(merchantId, amount);
            if (rows <= 0) {
                throw new RuntimeException("审核通过时更新账户失败");
            }
            
            log.info("提现审核通过 - 记录ID: {}, 商家ID: {}, 金额: {}", recordId, merchantId, amount);
        } else {
            // 拒绝提现
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("拒绝提现时必须提供原因");
            }
            
            record.reject(reason);
            
            // 更新商家账户：减少待提现，恢复可提现
            int rows = merchantAccountRepository.rejectWithdraw(merchantId, amount);
            if (rows <= 0) {
                throw new RuntimeException("拒绝提现时更新账户失败");
            }
            
            log.info("提现审核拒绝 - 记录ID: {}, 商家ID: {}, 金额: {}, 原因: {}", recordId, merchantId, amount, reason);
        }

        withdrawRecordRepository.save(record);
        return true;
    }

    // ====================== 工具：转DTO ======================
    private WithdrawRecordVO convertToVO(WithdrawRecord record) {
        WithdrawRecordVO vo = new WithdrawRecordVO();
        vo.setId(record.getId());
        vo.setAmount(record.getAmount());
        vo.setFee(record.getFee());
        vo.setActualAmount(record.getActualAmount());
        vo.setBankName(record.getBankName());
        vo.setStatus(record.getStatus());
        vo.setApplyTime(record.getApplyTime());
        return vo;
    }
}