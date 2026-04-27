package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 */
 @GetMapping("/account/{merchantId}")
 public ResponseResult<FinanceService.MerchantAccountDTO> getAccount( @PathVariable Long merchantId) {
 FinanceService.MerchantAccountDTO account = financeService.getAccount(merchantId);
 return ResponseResult.success(account);
 }

 /**
 * 获取财务数据
 */
 @GetMapping("/data/{merchantId}")
 public ResponseResult<FinanceService.FinanceDataDTO> getFinanceData( @PathVariable Long merchantId, @RequestParam String startDate, @RequestParam String endDate) {
 FinanceService.FinanceDataDTO data = financeService.getFinanceData(merchantId, startDate, endDate);
 return ResponseResult.success(data);
 }

 /**
 * 申请提现
 */
 @PostMapping("/withdraw/{merchantId}")
 public ResponseResult<Boolean> applyWithdraw( @PathVariable Long merchantId,
 @RequestBody FinanceService.WithdrawDTO withdrawDTO) {
 boolean result = financeService.applyWithdraw(merchantId, withdrawDTO);
 return ResponseResult.success(result);
 }

 /**
 * 获取提现记录
 */
 @GetMapping("/withdraw/{merchantId}")
 public ResponseResult<Page<FinanceService.WithdrawRecordDTO>> getWithdrawRecords( @PathVariable Long merchantId, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<FinanceService.WithdrawRecordDTO> page = financeService.getWithdrawRecords(merchantId, status, pageNum, pageSize);
 return ResponseResult.success(page);
 }
}
