package org.example.shoppingserver.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.shoppingserver.model.vo.finance.MerchantAccountVO;
import org.example.shoppingserver.model.vo.finance.FinanceDataVO;
import org.example.shoppingserver.model.dto.finance.WithdrawDTO;
import org.example.shoppingserver.model.vo.finance.WithdrawRecordVO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务管理Service接口
 */
public interface FinanceService {

    /**
     * 获取商家账户信息
     *
     * @param merchantId 商家ID
     * @return 账户信息
     */
    MerchantAccountVO getAccount(Long merchantId);

    /**
     * 获取财务数据
     *
     * @param merchantId 商家ID
     * @param startDate  开始日期 (格式: yyyy-MM-dd)
     * @param endDate   结束日期 (格式: yyyy-MM-dd)
     * @return 财务数据
     */
    FinanceDataVO getFinanceData(Long merchantId, String startDate, String endDate);

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
    Page<WithdrawRecordVO> getWithdrawRecords(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 审核提现申请
     *
     * @param recordId 提现记录ID
     * @param approved 是否批准
     * @param reason   拒绝原因（如果拒绝）
     * @return 是否成功
     */
    boolean auditWithdraw(Long recordId, boolean approved, String reason);

}
