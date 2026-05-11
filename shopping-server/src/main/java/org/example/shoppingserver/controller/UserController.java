package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.user.UserAddressDTO;
import org.example.shoppingserver.model.vo.user.UserAddressVO;

import org.example.shoppingserver.model.dto.user.UserDTO;
import org.example.shoppingserver.model.vo.user.UserVO;
import org.example.shoppingserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


 private static final Logger log = LoggerFactory.getLogger(UserController.class);
 private final UserService userService;


 /**
  * 获取当前用户信息
  */
 @GetMapping("/me")
 public ResponseResult<UserVO> getCurrentUser() {
  UserVO user = userService.getCurrentUser();
  log.info("获取用户信息：{}", user);
  return ResponseResult.success(user);
 }

 /**
  * 获取用户详情
  */
 @GetMapping()
 public ResponseResult<UserVO> getUserById() {
  UserVO user = userService.getUserById(UserHolder.getCurrentUserId());
  log.info("获取用户详情：{}", ResponseResult.success(user));
  return ResponseResult.success(user);
 }

 /**
  * 更新用户信息
  */
 @PutMapping()
 public ResponseResult<UserVO> updateUser(
         @RequestBody @Validated UserDTO userDTO) {
  UserVO user = userService.updateUser(SecurityContextHolder.
          getContext().getAuthentication().getName(), userDTO);
  return ResponseResult.success(user);
 }


 // ==================== 收货地址 ====================

 /**
  * 获取用户收货地址列表
  */
 @GetMapping("/addresses")
 public ResponseResult<List<UserAddressVO>> getAddresses() {
  List<UserAddressVO> addresses = userService.getAddresses(UserHolder.getCurrentUserId());
  return ResponseResult.success(addresses);
 }

 /**
  * 获取收货地址详情
  */
 @GetMapping("/addresses/{addressId}")
 public ResponseResult<UserAddressVO> getAddressById(
         @PathVariable Long addressId) {
  UserAddressVO address = userService.getAddressById(addressId);
  return ResponseResult.success(address);
 }

 /**
  * 添加收货地址
  */
 @PostMapping("/addresses")
 public ResponseResult<UserAddressVO> addAddress(
         @RequestBody @Validated UserAddressDTO addressDTO) {
  UserAddressVO address = userService.addAddress(UserHolder.getCurrentUserId(), addressDTO);
  return ResponseResult.success(address);
 }

 /**
  * 更新收货地址
  */
 @PutMapping("/addresses/{addressId}")
 public ResponseResult<UserAddressVO> updateAddress(
         @PathVariable Long addressId,
         @RequestBody @Validated UserAddressDTO addressDTO) {
  UserAddressVO address = userService.updateAddress(UserHolder.getCurrentUserId(), addressId, addressDTO);
  return ResponseResult.success(address);
 }

 /**
  * 删除收货地址
  */
 @DeleteMapping("/addresses/{addressId}")
 public ResponseResult<Boolean> deleteAddress(
         @PathVariable Long addressId) {
  boolean success = userService.deleteAddress(UserHolder.getCurrentUserId(), addressId);
  return ResponseResult.success(success);
 }

 /**
  * 设置默认收货地址
  */
 @PutMapping("/addresses/{addressId}/default")
 public ResponseResult<Boolean> setDefaultAddress(
         @PathVariable Long addressId) {
  boolean success = userService.setDefaultAddress(UserHolder.getCurrentUserId(), addressId);
  return ResponseResult.success(success);
 }
}
