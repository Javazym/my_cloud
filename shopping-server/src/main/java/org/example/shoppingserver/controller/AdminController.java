package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.result.ResponseResult;

import org.example.shoppingserver.model.dto.product.CategoryDTO;
import org.example.shoppingserver.model.vo.admin.PlatformStatisticsVO;
import org.example.shoppingserver.model.vo.finance.FinanceStatisticsVO;
import org.example.shoppingserver.model.vo.finance.WithdrawRecordVO;
import org.example.shoppingserver.model.vo.marketing.AnnouncementVO;
import org.example.shoppingserver.model.vo.marketing.BannerVO;
import org.example.shoppingserver.model.vo.merchant.MerchantApplicationVO;
import org.example.shoppingserver.model.dto.merchant.MerchantAuditDTO;
import org.example.shoppingserver.model.vo.merchant.MerchantVO;
import org.example.shoppingserver.model.vo.merchant.MerchantGroupVO;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.product.CategoryVO;
import org.example.shoppingserver.model.vo.product.ProductVO;
import org.example.shoppingserver.service.AdminService;
import org.example.shoppingserver.util.annotation.RequireRole;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.example.shoppingserver.model.vo.user.UserVO;
import java.util.List;

/**
 * 管理员控制器
 *
 * @author System
 * @since 2026-04-28
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@RequireRole(value = {"ROLE_ADMIN"})
public class AdminController {

 private final AdminService adminService;

// /**
// * 管理员登录
// */
// @PostMapping("/login")
// public ResponseResult<AdminDTO> login(@RequestParam String username, @RequestParam String password) {
// AdminDTO admin = adminService.login(username, password);
// return ResponseResult.success(admin);
// }
//
// /**
// * 获取当前登录管理员
// */
// @GetMapping("/current")
// public ResponseResult<AdminDTO> getCurrentAdmin() {
// AdminDTO admin = adminService.getCurrentAdmin();
// return ResponseResult.success(admin);
// }

 /**
 * 修改密码
 */
 @PutMapping("/password")
 public ResponseResult<Boolean> changePassword( @RequestParam Long adminId, @RequestParam String oldPassword, @RequestParam String newPassword) {
 boolean result = adminService.changePassword(adminId, oldPassword, newPassword);
 return ResponseResult.success(result);
 }

 /**
 * 创建管理员
 */
 @PostMapping("/create")
 public ResponseResult<Long> createAdmin(@RequestParam String username, @RequestParam String password) {
 Long adminId = adminService.createAdmin(username, password);
 return ResponseResult.success(adminId);
 }

 /**
 * 退出登录
 */
 @PostMapping("/logout")
 public ResponseResult<Void> logout() {
 adminService.logout();
 return ResponseResult.success();
 }

 /**
 * 获取平台统计数据
 */
 @GetMapping("/statistics")
 public ResponseResult<PlatformStatisticsVO> getPlatformStatistics() {
 PlatformStatisticsVO statistics = adminService.getPlatformStatistics();
 return ResponseResult.success(statistics);
 }

 // ==================== 商家入驻申请管理 ====================

 /**
 * 获取待审核的商家申请列表
 */
 @GetMapping("/merchant-applications/pending")
 public ResponseResult<Page<MerchantApplicationVO>> getPendingApplications(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationVO> applications = adminService.getPendingApplications(pageNum, pageSize);
 return ResponseResult.success(applications);
 }

 /**
 * 获取所有商家申请列表
 */
 @GetMapping("/merchant-applications/all")
 public ResponseResult<Page<MerchantApplicationVO>> getAllApplications( @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationVO> applications = adminService.getAllApplications(pageNum, pageSize);
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
 public ResponseResult<Page<MerchantVO>> getAllMerchants(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantVO> merchants = adminService.getAllMerchants(pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 根据状态获取商家列表
 */
 @GetMapping("/merchants/by-status")
 public ResponseResult<Page<MerchantVO>> getMerchantsByStatus( @RequestParam Integer status, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantVO> merchants = adminService.getMerchantsByStatus(status, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 根据审核状态获取商家列表
 */
 @GetMapping("/merchants/by-audit-status")
 public ResponseResult<Page<MerchantApplicationVO>> getMerchantsByAuditStatus( @RequestParam Integer auditStatus, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantApplicationVO> merchants = adminService.getMerchantsByAuditStatus(auditStatus, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 /**
 * 获取商家详情
 */
 @GetMapping("/merchants/{id}")
 public ResponseResult<MerchantVO> getMerchantDetail( @PathVariable Long id) {
 MerchantVO merchant = adminService.getMerchantDetail(id);
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
 public ResponseResult<List<MerchantGroupVO>> getMerchantsGroupedByCategory() {
 List<MerchantGroupVO> groups = adminService.getMerchantsGroupedByCategory();
 return ResponseResult.success(groups);
 }

 /**
 * 获取指定类目的商家列表
 */
 @GetMapping("/merchants/by-category")
 public ResponseResult<Page<MerchantVO>> getMerchantsByCategory( @RequestParam String category, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
 Page<MerchantVO> merchants = adminService.getMerchantsByCategory(category, pageNum, pageSize);
 return ResponseResult.success(merchants);
 }

 // ==================== 用户管理 ====================

 /**
  * 获取用户列表
  *
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 用户分页列表
  */
 @GetMapping("/users")
 public ResponseResult<Page<UserVO>> getUserList(
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<UserVO> users = adminService.getUserList(pageNum, pageSize);
     return ResponseResult.success(users);
 }

 /**
  * 根据状态获取用户列表
  *
  * @param status 用户状态
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 用户分页列表
  */
 @GetMapping("/users/by-status")
 public ResponseResult<Page<UserVO>> getUsersByStatus(
         @RequestParam Integer status,
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<UserVO> users = adminService.getUsersByStatus(status, pageNum, pageSize);
     return ResponseResult.success(users);
 }

 /**
  * 获取用户详情
  *
  * @param userId 用户ID
  * @return 用户详情
  */
 @GetMapping("/users/{userId}")
 public ResponseResult<UserVO> getUserDetail(@PathVariable String userId) {
     UserVO user = adminService.getUserDetail(userId);
     return ResponseResult.success(user);
 }

 /**
  * 更新用户状态
  *
  * @param userId 用户ID
  * @param status 状态
  * @return 操作结果
  */
 @PutMapping("/users/{userId}/status")
 public ResponseResult<Boolean> updateUserStatus(
         @PathVariable String userId,
         @RequestParam Integer status) {
     boolean result = adminService.updateUserStatus(userId, status);
     return ResponseResult.success(result);
 }

 /**
  * 删除用户
  *
  * @param userId 用户ID
  * @return 操作结果
  */
 @DeleteMapping("/users/{userId}")
 public ResponseResult<Boolean> deleteUser(@PathVariable String userId) {
     boolean result = adminService.deleteUser(userId);
     return ResponseResult.success(result);
 }

 // ==================== 订单管理 ====================

 /**
  * 获取订单列表
  *
  * @param status 订单状态（可选）
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 订单分页列表
  */
 @GetMapping("/orders")
 public ResponseResult<Page<OrderVO>> getOrderList(
         @RequestParam(required = false) Integer status,
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<OrderVO> orders = adminService.getOrderList(status, pageNum, pageSize);
     return ResponseResult.success(orders);
 }

 /**
  * 获取订单详情
  *
  * @param orderId 订单ID
  * @return 订单详情
  */
 @GetMapping("/orders/{orderId}")
 public ResponseResult<OrderVO> getOrderDetail(@PathVariable Long orderId) {
     OrderVO order = adminService.getOrderDetail(orderId);
     return ResponseResult.success(order);
 }

 /**
  * 取消订单
  *
  * @param orderId 订单ID
  * @return 操作结果
  */
 @PutMapping("/orders/{orderId}/cancel")
 public ResponseResult<Boolean> cancelOrder(@PathVariable Long orderId) {
     boolean result = adminService.cancelOrder(orderId);
     return ResponseResult.success(result);
 }

 /**
  * 强制完成订单
  *
  * @param orderId 订单ID
  * @return 操作结果
  */
 @PutMapping("/orders/{orderId}/complete")
 public ResponseResult<Boolean> forceCompleteOrder(@PathVariable Long orderId) {
     boolean result = adminService.forceCompleteOrder(orderId);
     return ResponseResult.success(result);
 }

 // ==================== 商品审核 ====================

 /**
  * 获取待审核商品列表
  *
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 商品分页列表
  */
 @GetMapping("/products/pending")
 public ResponseResult<Page<ProductVO>> getPendingProducts(
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<ProductVO> products = adminService.getPendingProducts(pageNum, pageSize);
     return ResponseResult.success(products);
 }

 /**
  * 获取所有商品列表
  *
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 商品分页列表
  */
 @GetMapping("/products")
 public ResponseResult<Page<ProductVO>> getAllProducts(
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<ProductVO> products = adminService.getAllProducts(pageNum, pageSize);
     return ResponseResult.success(products);
 }

 /**
  * 获取商品详情
  *
  * @param productId 商品ID
  * @return 商品详情
  */
 @GetMapping("/products/{productId}")
 public ResponseResult<ProductVO> getProductDetail(@PathVariable Long productId) {
     ProductVO product = adminService.getProductDetail(productId);
     return ResponseResult.success(product);
 }

 /**
  * 审核商品
  *
  * @param productId 商品ID
  * @param approved 是否通过
  * @param reason 审核原因
  * @return 操作结果
  */
 @PostMapping("/products/{productId}/audit")
 public ResponseResult<Boolean> auditProduct(
         @PathVariable Long productId,
         @RequestParam Boolean approved,
         @RequestParam(required = false) String reason) {
     boolean result = adminService.auditProduct(productId, approved, reason);
     return ResponseResult.success(result);
 }

 /**
  * 下架商品
  *
  * @param productId 商品ID
  * @return 操作结果
  */
 @PutMapping("/products/{productId}/offline")
 public ResponseResult<Boolean> offlineProduct(@PathVariable Long productId) {
     boolean result = adminService.offlineProduct(productId);
     return ResponseResult.success(result);
 }

 // ==================== 分类管理 ====================

 /**
  * 获取分类树
  *
  * @return 分类树
  */
 @GetMapping("/categories/tree")
 public ResponseResult<List<CategoryVO>> getCategoryTree() {
     List<CategoryVO> tree = adminService.getCategoryTree();
     return ResponseResult.success(tree);
 }

 /**
  * 创建分类
  *
  * @param dto 分类信息
  * @return 分类ID
  */
 @PostMapping("/categories")
 public ResponseResult<Long> createCategory(@RequestBody CategoryDTO dto) {
     Long categoryId = adminService.createCategory(dto);
     return ResponseResult.success(categoryId);
 }

 /**
  * 更新分类
  *
  * @param categoryId 分类ID
  * @param dto 分类信息
  * @return 操作结果
  */
 @PutMapping("/categories/{categoryId}")
 public ResponseResult<Void> updateCategory(
         @PathVariable Long categoryId,
         @RequestBody CategoryDTO dto) {
     adminService.updateCategory(categoryId, dto);
     return ResponseResult.success();
 }

 /**
  * 删除分类
  *
  * @param categoryId 分类ID
  * @return 操作结果
  */
 @DeleteMapping("/categories/{categoryId}")
 public ResponseResult<Void> deleteCategory(@PathVariable Long categoryId) {
     adminService.deleteCategory(categoryId);
     return ResponseResult.success();
 }

 // ==================== 财务管理 ====================

 /**
  * 获取提现申请列表
  *
  * @param status 状态（可选）
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 提现申请分页列表
  */
 @GetMapping("/finance/withdraws")
 public ResponseResult<Page<WithdrawRecordVO>> getWithdrawRecords(
         @RequestParam(required = false) Integer status,
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<WithdrawRecordVO> records = adminService.getWithdrawRecords(status, pageNum, pageSize);
     return ResponseResult.success(records);
 }

 /**
  * 审核提现申请
  *
  * @param withdrawId 提现ID
  * @param approved 是否通过
  * @param reason 审核原因
  * @return 操作结果
  */
 @PostMapping("/finance/withdraws/{withdrawId}/audit")
 public ResponseResult<Boolean> auditWithdraw(
         @PathVariable Long withdrawId,
         @RequestParam Boolean approved,
         @RequestParam(required = false) String reason) {
     boolean result = adminService.auditWithdraw(withdrawId, approved, reason);
     return ResponseResult.success(result);
 }

 /**
  * 获取财务统计
  *
  * @return 财务统计数据
  */
 @GetMapping("/finance/statistics")
 public ResponseResult<FinanceStatisticsVO> getFinanceStatistics() {
     FinanceStatisticsVO statistics = adminService.getFinanceStatistics();
     return ResponseResult.success(statistics);
 }

 // ==================== 营销管理（轮播图、公告）====================

 /**
  * 获取轮播图列表
  *
  * @return 轮播图列表
  */
 @GetMapping("/marketing/banners")
 public ResponseResult<List<BannerVO>> getBanners() {
     List<BannerVO> banners = adminService.getBanners();
     return ResponseResult.success(banners);
 }

 /**
  * 创建轮播图
  *
  * @param dto 轮播图信息
  * @return 轮播图ID
  */
 @PostMapping("/marketing/banners")
 public ResponseResult<Long> createBanner(@RequestBody BannerVO dto) {
     Long bannerId = adminService.createBanner(dto);
     return ResponseResult.success(bannerId);
 }

 /**
  * 更新轮播图
  *
  * @param bannerId 轮播图ID
  * @param dto 轮播图信息
  * @return 操作结果
  */
 @PutMapping("/marketing/banners/{bannerId}")
 public ResponseResult<Void> updateBanner(
         @PathVariable Long bannerId,
         @RequestBody BannerVO dto) {
     adminService.updateBanner(bannerId, dto);
     return ResponseResult.success();
 }

 /**
  * 删除轮播图
  *
  * @param bannerId 轮播图ID
  * @return 操作结果
  */
 @DeleteMapping("/marketing/banners/{bannerId}")
 public ResponseResult<Void> deleteBanner(@PathVariable Long bannerId) {
     adminService.deleteBanner(bannerId);
     return ResponseResult.success();
 }

 /**
  * 更新轮播图状态
  *
  * @param bannerId 轮播图ID
  * @param status 状态
  * @return 操作结果
  */
 @PutMapping("/marketing/banners/{bannerId}/status")
 public ResponseResult<Void> updateBannerStatus(
         @PathVariable Long bannerId,
         @RequestParam Integer status) {
     adminService.updateBannerStatus(bannerId, status);
     return ResponseResult.success();
 }

 /**
  * 获取公告列表
  *
  * @param type 公告类型（可选）
  * @return 公告列表
  */
 @GetMapping("/marketing/announcements")
 public ResponseResult<List<AnnouncementVO>> getAnnouncements(
         @RequestParam(required = false) Integer type) {
     List<AnnouncementVO> announcements = adminService.getAnnouncements(type);
     return ResponseResult.success(announcements);
 }

 /**
  * 创建公告
  *
  * @param dto 公告信息
  * @return 公告ID
  */
 @PostMapping("/marketing/announcements")
 public ResponseResult<Long> createAnnouncement(@RequestBody AnnouncementVO dto) {
     Long announcementId = adminService.createAnnouncement(dto);
     return ResponseResult.success(announcementId);
 }

 /**
  * 更新公告
  *
  * @param announcementId 公告ID
  * @param dto 公告信息
  * @return 操作结果
  */
 @PutMapping("/marketing/announcements/{announcementId}")
 public ResponseResult<Void> updateAnnouncement(
         @PathVariable Long announcementId,
         @RequestBody AnnouncementVO dto) {
     adminService.updateAnnouncement(announcementId, dto);
     return ResponseResult.success();
 }

 /**
  * 删除公告
  *
  * @param announcementId 公告ID
  * @return 操作结果
  */
 @DeleteMapping("/marketing/announcements/{announcementId}")
 public ResponseResult<Void> deleteAnnouncement(@PathVariable Long announcementId) {
     adminService.deleteAnnouncement(announcementId);
     return ResponseResult.success();
 }

 /**
  * 更新公告状态
  *
  * @param announcementId 公告ID
  * @param status 状态
  * @return 操作结果
  */
 @PutMapping("/marketing/announcements/{announcementId}/status")
 public ResponseResult<Void> updateAnnouncementStatus(
         @PathVariable Long announcementId,
         @RequestParam Integer status) {
     adminService.updateAnnouncementStatus(announcementId, status);
     return ResponseResult.success();
 }
}