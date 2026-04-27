package org.example.authserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.authserver.common.ResponseResult;
import org.example.authserver.dto.create.RegisterDto;
import org.example.authserver.dto.query.AuthCodeDto;
import org.example.authserver.dto.query.LoginDto;
import org.example.authserver.service.AuthService;
import org.example.authserver.service.EmailService;
import org.example.authserver.util.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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
}
