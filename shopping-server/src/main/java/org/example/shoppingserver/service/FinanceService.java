package org.example.shoppingserver.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 财务Service接口
 */

public interface FinanceService {

    /**
     * 获取商家账户信息
     *
     * @param merchantId 商家ID
     * @return 账户信息
     */
    MerchantAccountDTO getAccount(Long merchantId);

    /**
     * 获取财务数据
     *
     * @param merchantId 商家ID
     * @param startDate  开始日期
     * @param endDate   结束日期
     * @return 财务数据
     */
    FinanceDataDTO getFinanceData(Long merchantId, String startDate, String endDate);

    /**
     * 申请提现
     *
     * @param merchantId   商家ID
     * @param withdrawDTO  提现DTO
     * @return 是否成功
     */
    boolean applyWithdraw(Long merchantId, WithdrawDTO withdrawDTO);

    /**
     * 获取提现记录
     *
     * @param merchantId 商家ID
     * @param status     状态
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 提现记录分页结果
     */
    Page<WithdrawRecordDTO> getWithdrawRecords(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 商家账户DTO
     */
    class MerchantAccountDTO {
        private Long merchantId;
        private java.math.BigDecimal totalIncome;
        private java.math.BigDecimal withdrawable;
        private java.math.BigDecimal withdrawn;
        private java.math.BigDecimal pendingWithdraw;

        public Long getMerchantId() { return merchantId; }
        public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
        public java.math.BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(java.math.BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public java.math.BigDecimal getWithdrawable() { return withdrawable; }
        public void setWithdrawable(java.math.BigDecimal withdrawable) { this.withdrawable = withdrawable; }
        public java.math.BigDecimal getWithdrawn() { return withdrawn; }
        public void setWithdrawn(java.math.BigDecimal withdrawn) { this.withdrawn = withdrawn; }
        public java.math.BigDecimal getPendingWithdraw() { return pendingWithdraw; }
        public void setPendingWithdraw(java.math.BigDecimal pendingWithdraw) { this.pendingWithdraw = pendingWithdraw; }
    }

    /**
     * 财务数据DTO
     */
    class FinanceDataDTO {
        private java.math.BigDecimal totalIncome;
        private java.math.BigDecimal monthIncome;
        private java.math.BigDecimal todayIncome;
        private Long orderCount;
        private Long monthOrderCount;
        private java.math.BigDecimal averageOrderAmount;
        private java.math.BigDecimal refundAmount;

        public java.math.BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(java.math.BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public java.math.BigDecimal getMonthIncome() { return monthIncome; }
        public void setMonthIncome(java.math.BigDecimal monthIncome) { this.monthIncome = monthIncome; }
        public java.math.BigDecimal getTodayIncome() { return todayIncome; }
        public void setTodayIncome(java.math.BigDecimal todayIncome) { this.todayIncome = todayIncome; }
        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
        public Long getMonthOrderCount() { return monthOrderCount; }
        public void setMonthOrderCount(Long monthOrderCount) { this.monthOrderCount = monthOrderCount; }
        public java.math.BigDecimal getAverageOrderAmount() { return averageOrderAmount; }
        public void setAverageOrderAmount(java.math.BigDecimal averageOrderAmount) { this.averageOrderAmount = averageOrderAmount; }
        public java.math.BigDecimal getRefundAmount() { return refundAmount; }
        public void setRefundAmount(java.math.BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    }

    /**
     * 提现DTO
     */
    class WithdrawDTO {
        private java.math.BigDecimal amount;
        private String bankName;
        private String bankAccount;
        private String accountName;

        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getBankAccount() { return bankAccount; }
        public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
    }

    /**
     * 提现记录DTO
     */
    class WithdrawRecordDTO {
        private Long id;
        private java.math.BigDecimal amount;
        private java.math.BigDecimal fee;
        private java.math.BigDecimal actualAmount;
        private String bankName;
        private String bankAccount;
        private Integer status;
        private String statusText;
        private java.time.LocalDateTime applyTime;
        private java.time.LocalDateTime processTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        public java.math.BigDecimal getFee() { return fee; }
        public void setFee(java.math.BigDecimal fee) { this.fee = fee; }
        public java.math.BigDecimal getActualAmount() { return actualAmount; }
        public void setActualAmount(java.math.BigDecimal actualAmount) { this.actualAmount = actualAmount; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getBankAccount() { return bankAccount; }
        public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getStatusText() { return statusText; }
        public void setStatusText(String statusText) { this.statusText = statusText; }
        public java.time.LocalDateTime getApplyTime() { return applyTime; }
        public void setApplyTime(java.time.LocalDateTime applyTime) { this.applyTime = applyTime; }
        public java.time.LocalDateTime getProcessTime() { return processTime; }
        public void setProcessTime(java.time.LocalDateTime processTime) { this.processTime = processTime; }
    }
}
