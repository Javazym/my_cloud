package org.example.authserver.service.impl;

import lombok.SneakyThrows;
import org.example.authserver.dto.create.RegisterDto;
import org.example.authserver.dto.query.AuthCodeDto;
import org.example.authserver.dto.query.LoginDto;
import org.example.authserver.entity.Role;
import org.example.authserver.entity.SysUser;
import org.example.authserver.repository.AuthRepository;
import org.example.authserver.repository.RoleRepository;
import org.example.authserver.service.AuthService;
import org.example.authserver.service.EmailService;
import org.example.authserver.util.JwtGenerator;
import org.example.authserver.util.PasswordEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private EmailService emailService;

    @SneakyThrows
    @Override
    public String login(LoginDto dto) {
        SysUser sysUser = null;
        if (!dto.getEmail().isEmpty()) {
            sysUser = authRepository.findByEmail(dto.getEmail());
        }
        if (!dto.getUsername().isEmpty()) {
            sysUser = authRepository.findByUsername(dto.getUsername());
        }
        log.info(dto.getEmail());
        log.info(dto.getUsername());
        if (sysUser != null && passwordEncoderUtil
                .matches(dto.getPassword(), sysUser.getPassword())) {

            return jwtGenerator.generateToken(sysUser.getId(), sysUser.getUsername(), sysUser.getRoles());
        }
        return "error";
    }
    @SneakyThrows
    @Override
    public String authCode(AuthCodeDto dto) {
        SysUser sysUser = authRepository.findByEmail(dto.getEmail());
        if (sysUser == null) {
            return "error";
        }
        if (!emailService.verifyCode(dto.getEmail(), dto.getCode())) {
            return "error";
        }
        return jwtGenerator.generateToken(sysUser.getId(), sysUser.getUsername(), sysUser.getRoles());
    }

    @Override
    public String register(RegisterDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            log.info("两次密码不一致");
            return "error";
        }
        if (authRepository.findByUsername(dto.getUsername()) != null) {
            log.info("用户名已存在");
            return "error";
        }
        if (!emailService.verifyCode(dto.getEmail(), dto.getCode())) {
            log.info("验证码失效");
            return "error";
        }
        SysUser sysUser = new SysUser();
        sysUser.setUsername(dto.getUsername());
        sysUser.setPassword(passwordEncoderUtil.encode(dto.getPassword()));
        sysUser.setEmail(dto.getEmail());
        Role role = roleRepository.findById(3L).get();
        role.getUsers().add(sysUser);
        sysUser.getRoles().add(role);
        authRepository.save(sysUser);
        return "注册成功";
    }
}
