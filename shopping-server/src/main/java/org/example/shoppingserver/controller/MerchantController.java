package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.MerchantApplicationDTO;
import org.example.shoppingserver.model.dto.MerchantDTO;
import org.example.shoppingserver.model.entity.MerchantApplication;
import org.example.shoppingserver.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
 @RequestBody MerchantService.ApplySettleDTO applyDTO) {
 boolean result = merchantService.applySettle(UserHolder.getCurrentUserId(), applyDTO);
 return ResponseResult.success(result);
 }

 /**
 * 获取商家信息
 */
 @GetMapping()
 public ResponseResult<MerchantDTO> getMerchantInfo() {
 MerchantDTO merchant = merchantService.getMerchantInfo(UserHolder.getCurrentUserId());
 return ResponseResult.success(merchant);
 }

 /**
 * 获取商家入驻信息
 */
 @GetMapping("/apply")
 public ResponseResult<MerchantApplicationDTO> getApplyInfo() {
 return ResponseResult.success(merchantService.
 getMerchantApplication(UserHolder.getCurrentUserId()));
 }

 /**
 * 根据用户ID获取商家信息
 */
 @GetMapping("/by-user")
 public ResponseResult<MerchantDTO> getMerchantByUserId() {
 MerchantDTO merchant = merchantService.getMerchantByUserId(UserHolder.getCurrentUserId());
 return ResponseResult.success(merchant);
 }

 /**
 * 更新商家信息
 */
 @PutMapping("/{merchantId}")
 public ResponseResult<MerchantDTO> updateMerchantInfo(
 @RequestBody MerchantDTO merchantDTO) {
 MerchantDTO merchant = merchantService.updateMerchantInfo(UserHolder.getCurrentUserId(), merchantDTO);
 return ResponseResult.success(merchant);
 }

 /**
 * 获取商家统计数据
 */
 @GetMapping("/{merchantId}/statistics")
 public ResponseResult<MerchantService.MerchantStatisticsDTO> getStatistics() {
 MerchantService.MerchantStatisticsDTO statistics = merchantService.getStatistics(UserHolder.getCurrentUserId());
 return ResponseResult.success(statistics);
 }
}
