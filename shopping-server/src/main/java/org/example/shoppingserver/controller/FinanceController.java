package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.vo.finance.MerchantAccountVO;
import org.example.shoppingserver.model.vo.finance.FinanceDataVO;
import org.example.shoppingserver.model.dto.finance.WithdrawDTO;
import org.example.shoppingserver.model.vo.finance.WithdrawRecordVO;
import org.example.shoppingserver.service.FinanceService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 财务控制器
 */
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /**
     * 获取商家账户信息
     *
     * @param merchantId 商家ID
     * @return 账户信息
     */
    @GetMapping("/account/{merchantId}")
    public ResponseResult<MerchantAccountVO> getAccount(
            @PathVariable Long merchantId) {
        MerchantAccountVO account = financeService.getAccount(merchantId);
        return ResponseResult.success(account);
    }

    /**
     * 获取财务数据
     *
     * @param merchantId 商家ID
     * @param startDate  开始日期 (格式: yyyy-MM-dd)
     * @param endDate   结束日期 (格式: yyyy-MM-dd)
     * @return 财务数据
     */
    @GetMapping("/data/{merchantId}")
    public ResponseResult<FinanceDataVO> getFinanceData(
            @PathVariable Long merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        FinanceDataVO data = financeService.getFinanceData(merchantId, startDate, endDate);
        return ResponseResult.success(data);
    }

    /**
     * 申请提现
     *
     * @param merchantId   商家ID
     * @param withdrawDTO  提现DTO
     * @return 是否成功
     */
    @PostMapping("/withdraw/{merchantId}")
    public ResponseResult<Boolean> applyWithdraw(
            @PathVariable Long merchantId,
            @Validated @RequestBody WithdrawDTO withdrawDTO) {
        boolean result = financeService.applyWithdraw(merchantId, withdrawDTO);
        return ResponseResult.success(result);
    }

    /**
     * 获取提现记录
     *
     * @param merchantId 商家ID
     * @param status     状态 (0-待审核, 1-审核通过, 2-已打款, 3-已拒绝)
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 提现记录分页结果
     */
    @GetMapping("/withdraw/{merchantId}")
    public ResponseResult<Page<WithdrawRecordVO>> getWithdrawRecords(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<WithdrawRecordVO> page = financeService.getWithdrawRecords(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(page);
    }
    /**
     * 审核提现申请
     *
     * @param recordId 提现记录ID
     * @param approved 是否批准
     * @param reason   拒绝原因（如果拒绝）
     * @return 是否成功
     */
    @PostMapping("/audit/{recordId}")
    public ResponseResult<Boolean> auditWithdraw(
            @PathVariable Long recordId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        boolean result = financeService.auditWithdraw(recordId, approved, reason);
        return ResponseResult.success(result);
    }
}
