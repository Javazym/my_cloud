package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.merchant.*;
import org.example.shoppingserver.model.vo.merchant.MerchantVO;
import org.example.shoppingserver.model.vo.merchant.MerchantApplicationVO;
import org.example.shoppingserver.model.vo.merchant.MerchantStatisticsVO;
import org.example.shoppingserver.service.MerchantService;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {


 private final MerchantService merchantService;

 /**
 * 申请入驻
 */
 @PostMapping("/apply")
 public ResponseResult<Boolean> applySettle(
 @RequestBody ApplySettleDTO applyDTO) {
 boolean result = merchantService.applySettle(UserHolder.getCurrentUserId(), applyDTO);
 return ResponseResult.success(result);
 }

 /**
 * 获取商家信息
 */
 @GetMapping()
 public ResponseResult<MerchantVO> getMerchantInfo() {
 MerchantVO merchant = merchantService.getMerchantInfo(UserHolder.getCurrentUserId());
 return ResponseResult.success(merchant);
 }

 /**
 * 获取商家入驻信息
 */
 @GetMapping("/apply")
 public ResponseResult<MerchantApplicationVO> getApplyInfo() {
 return ResponseResult.success(merchantService.
 getMerchantApplication(UserHolder.getCurrentUserId()));
 }

 /**
 * 根据用户ID获取商家信息
 */
 @GetMapping("/by-user")
 public ResponseResult<MerchantVO> getMerchantByUserId() {
 MerchantVO merchant = merchantService.getMerchantByUserId(UserHolder.getCurrentUserId());
 return ResponseResult.success(merchant);
 }

 /**
 * 更新商家信息
 */
 @PutMapping("/{merchantId}")
 public ResponseResult<MerchantVO> updateMerchantInfo(
 @RequestBody MerchantDTO merchantDTO) {
 MerchantVO merchant = merchantService.updateMerchantInfo(UserHolder.getCurrentUserId(), merchantDTO);
 return ResponseResult.success(merchant);
 }

 /**
 * 获取商家统计数据
 */
 @GetMapping("/{merchantId}/statistics")
 public ResponseResult<MerchantStatisticsVO> getStatistics() {
 MerchantStatisticsVO statistics = merchantService.getStatistics(UserHolder.getCurrentUserId());
 return ResponseResult.success(statistics);
 }
}
