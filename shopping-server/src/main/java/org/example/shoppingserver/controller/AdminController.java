package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.*;
import org.example.shoppingserver.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

 private final AdminService adminService;

 /**
 * 管理员登录
 */
 @PostMapping("/login")
 public ResponseResult<AdminDTO> login( @RequestParam String username, @RequestParam String password) {
 AdminDTO admin = adminService.login(username, password);
 return ResponseResult.success(admin);
 }

 /**
 * 获取当前登录管理员
 */
 @GetMapping("/current")
 public ResponseResult<AdminDTO> getCurrentAdmin() {
 AdminDTO admin = adminService.getCurrentAdmin();
 return ResponseResult.success(admin);
 }

 /**
 * 修改密码
 */
 @PutMapping("/password")
 public ResponseResult<Boolean> changePassword( @RequestParam Long adminId, @RequestParam String oldPassword, @RequestParam String newPassword) {
 boolean result = adminService.changePassword(adminId, oldPassword, newPassword);
 return ResponseResult.success(result);
 }

 /**
 * 获取平台统计数据
 */
 @GetMapping("/statistics")
 public ResponseResult<AdminService.PlatformStatisticsDTO> getPlatformStatistics() {
 AdminService.PlatformStatisticsDTO statistics = adminService.getPlatformStatistics();
 return ResponseResult.success(statistics);
 }

 // ==================== 商家入驻申请管理 ====================

 /**
 * 获取待审核的商家申请列表
 */
 @GetMapping("/merchant-applications/pending")
 public ResponseResult<Page<MerchantApplicationDTO>> getPendingApplications( @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationDTO> applications = adminService.getPendingApplications(pageNum, pageSize);
 return ResponseResult.success(applications);
 }

 /**
 * 获取所有商家申请列表
 */
 @GetMapping("/merchant-applications/all")
 public ResponseResult<Page<MerchantApplicationDTO>> getAllApplications( @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationDTO> applications = adminService.getAllApplications(pageNum, pageSize);
 return ResponseResult.success(applications);
 }

 /**
 * 审核商家入驻申请
 */
 @PostMapping("/merchant-applications/audit")
 public ResponseResult<Boolean> auditMerchantApplication(@RequestBody MerchantAuditDTO auditDTO) {
 boolean result = adminService.auditMerchantApplication(auditDTO);
 return ResponseResult.success(result);
 }

 // ==================== 商家管理 ====================

 /**
 * 获取所有商家列表
 */
 @GetMapping("/merchants/all")
 public ResponseResult<Page<MerchantDTO>> getAllMerchants( @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantDTO> merchants = adminService.getAllMerchants(pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 根据状态获取商家列表
 */
 @GetMapping("/merchants/by-status")
 public ResponseResult<Page<MerchantDTO>> getMerchantsByStatus( @RequestParam Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantDTO> merchants = adminService.getMerchantsByStatus(status, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 根据审核状态获取商家列表
 */
 @GetMapping("/merchants/by-audit-status")
 public ResponseResult<Page<MerchantApplicationDTO>> getMerchantsByAuditStatus( @RequestParam Integer auditStatus, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationDTO> merchants = adminService.getMerchantsByAuditStatus(auditStatus, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 获取商家详情
 */
 @GetMapping("/merchants/{id}")
 public ResponseResult<MerchantDTO> getMerchantDetail( @PathVariable Long id) {
 MerchantDTO merchant = adminService.getMerchantDetail(id);
 return ResponseResult.success(merchant);
 }

 /**
 * 更新商家状态
 */
 @PutMapping("/merchants/{id}/status")
 public ResponseResult<Boolean> updateMerchantStatus( @PathVariable Long id, @RequestParam Integer status) {
 boolean result = adminService.updateMerchantStatus(id, status);
 return ResponseResult.success(result);
 }

 /**
 * 删除商家
 */
 @DeleteMapping("/merchants/{id}")
 public ResponseResult<Boolean> deleteMerchant( @PathVariable Long id) {
 boolean result = adminService.deleteMerchant(id);
 return ResponseResult.success(result);
 }

 // ==================== 商家分组展示 ====================

 /**
 * 按主营类目分组展示商家
 */
 @GetMapping("/merchants/grouped")
 public ResponseResult<List<MerchantGroupDTO>> getMerchantsGroupedByCategory() {
 List<MerchantGroupDTO> groups = adminService.getMerchantsGroupedByCategory();
 return ResponseResult.success(groups);
 }

 /**
 * 获取指定类目的商家列表
 */
 @GetMapping("/merchants/by-category")
 public ResponseResult<Page<MerchantDTO>> getMerchantsByCategory( @RequestParam String category, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantDTO> merchants = adminService.getMerchantsByCategory(category, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }
}
