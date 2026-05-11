package org.example.authserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.authserver.common.ResponseResult;
import org.example.authserver.model.dto.create.AdminCreateDto;
import org.example.authserver.model.dto.create.RegisterDto;
import org.example.authserver.model.dto.query.AuthCodeDto;
import org.example.authserver.model.dto.query.LoginDto;
import org.example.authserver.model.dto.query.UserQueryDto;
import org.example.authserver.model.dto.update.UserUpdateDto;
import org.example.authserver.model.entity.SysUser;
import org.example.authserver.service.AuthService;
import org.example.authserver.service.EmailService;
import org.example.authserver.util.UserConverter;
import org.example.authserver.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody LoginDto dto) {
        log.info(dto.getEmail());
        String msg = authService.login(dto);
        if ("error".equals(msg)) {
            return ResponseResult.error("登录失败");
        }
        return ResponseResult.success(msg);
    }
    @PostMapping("/register")
    public ResponseResult<?> register(@RequestBody RegisterDto dto) {
        String msg = authService.register(dto);
        if ("error".equals(msg)) {
            return ResponseResult.error("注册失败");
        }
        return ResponseResult.success(authService.register(dto));
    }

    @PostMapping("/auth-code")
    public ResponseResult<?> authCode(@RequestBody AuthCodeDto dto) {
        String msg = authService.authCode(dto);
        if ("error".equals(msg)) {
            return ResponseResult.error("登录失败");
        }
        return ResponseResult.success(msg);
    }
    @GetMapping("/send-code")
    public ResponseResult<?> sendCode(@RequestParam String email) {
        return ResponseResult.success(emailService.sendVerificationCode(email));
    }
    
    // 管理员相关接口
    @PostMapping("/admin/create")
    public ResponseResult<?> createAdmin(@RequestBody AdminCreateDto dto) {
        String msg = authService.createAdmin(dto);
        if ("error".equals(msg)) {
            return ResponseResult.error("创建管理员失败");
        }
        return ResponseResult.success(msg);
    }
    
    // 用户管理相关接口
    @GetMapping("/users")
    public ResponseResult<?> getUsers(UserQueryDto queryDto) {
        Page<SysUser> users = authService.getUsers(queryDto);
        // 转换为VO对象，隐藏敏感信息
        Page<UserVO> userVOs = users.map(UserConverter::convertToVO);
        return ResponseResult.success(userVOs);
    }
    
    @GetMapping("/users/{id}")
    public ResponseResult<?> getUserById(@PathVariable String id) {
        SysUser user = authService.getUserById(id);
        if (user == null) {
            return ResponseResult.error("用户不存在");
        }
        UserVO userVO = UserConverter.convertToVO(user);
        return ResponseResult.success(userVO);
    }
    
    @PutMapping("/users")
    public ResponseResult<?> updateUser(@RequestBody UserUpdateDto dto) {
        String msg = authService.updateUser(dto);
        if ("error".equals(msg)) {
            return ResponseResult.error("更新用户信息失败");
        }
        return ResponseResult.success(msg);
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseResult<?> deleteUser(@PathVariable String id) {
        String msg = authService.deleteUser(id);
        if ("error".equals(msg)) {
            return ResponseResult.error("删除用户失败");
        }
        return ResponseResult.success(msg);
    }
    
    @PutMapping("/users/{id}/status")
    public ResponseResult<?> updateUserStatus(@PathVariable String id, @RequestParam Integer status) {
        String msg = authService.updateUserStatus(id, status);
        if ("error".equals(msg)) {
            return ResponseResult.error("更新用户状态失败");
        }
        return ResponseResult.success(msg);
    }
}
