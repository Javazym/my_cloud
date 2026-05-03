package org.example.authserver.service.impl;

import lombok.SneakyThrows;
import org.example.authserver.common.MessageWrapper;
import org.example.authserver.dto.create.AdminCreateDto;
import org.example.authserver.dto.create.RegisterDto;
import org.example.authserver.dto.query.AuthCodeDto;
import org.example.authserver.dto.query.LoginDto;
import org.example.authserver.dto.query.UserQueryDto;
import org.example.authserver.dto.update.UserUpdateDto;
import org.example.authserver.entity.Role;
import org.example.authserver.entity.SysUser;
import org.example.authserver.mq.AuthProducer;
import org.example.authserver.repository.AuthRepository;
import org.example.authserver.repository.RoleRepository;
import org.example.authserver.service.AuthService;
import org.example.authserver.service.EmailService;
import org.example.authserver.util.JwtGenerator;
import org.example.authserver.util.PasswordEncoderUtil;
import org.example.authserver.util.UserConverter;
import org.example.authserver.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Autowired
    private AuthProducer authProducer;

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
        UserVO userVO = UserConverter.convertToVO(sysUser);
        authProducer.send(MessageWrapper.<UserVO>builder()
                        .data(userVO)
                        .targetService("auth-server")
                .build());
        return "注册成功";
    }

    @Override
    public String createAdmin(AdminCreateDto dto) {
        // 验证密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            log.info("两次密码不一致");
            return "error";
        }
        
        // 检查用户名是否已存在
        if (authRepository.findByUsername(dto.getUsername()) != null) {
            log.info("用户名已存在");
            return "error";
        }
        
        // 检查邮箱是否已存在
        if (authRepository.findByEmail(dto.getEmail()) != null) {
            log.info("邮箱已存在");
            return "error";
        }
        

        
        // 创建管理员用户
        SysUser adminUser = new SysUser();
        adminUser.setUsername(dto.getUsername());
        adminUser.setPassword(passwordEncoderUtil.encode(dto.getPassword()));
        adminUser.setEmail(dto.getEmail());
        adminUser.setStatus(1); // 默认启用状态
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateTime(LocalDateTime.now());
        
        // 分配管理员角色（假设角色ID为1的是管理员角色）
        Role adminRole = roleRepository.findByName("ADMIN");
        if (adminRole == null) {
            // 如果没有找到ADMIN角色，尝试使用ID为1的角色
            adminRole = roleRepository.findById(1L).orElse(null);
        }
        
        if (adminRole != null) {
            adminUser.getRoles().add(adminRole);
            adminRole.getUsers().add(adminUser);
        } else {
            log.warn("未找到管理员角色，使用默认角色");
            // 如果找不到管理员角色，使用原来的逻辑
            Role defaultRole = roleRepository.findById(3L).orElse(null);
            if (defaultRole != null) {
                adminUser.getRoles().add(defaultRole);
                defaultRole.getUsers().add(adminUser);
            }
        }
        
        authRepository.save(adminUser);
        return "管理员创建成功";
    }

    @Override
    public Page<SysUser> getUsers(UserQueryDto queryDto) {
        // 创建分页对象
        Pageable pageable = PageRequest.of(
            queryDto.getPageNum() - 1, 
            queryDto.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 这里可以根据需要添加查询条件
        // 目前先返回所有用户的分页数据
        return authRepository.findAll(pageable);
    }

    @Override
    public SysUser getUserById(String id) {
        return authRepository.findById(id).orElse(null);
    }

    @Override
    public String updateUser(UserUpdateDto dto) {
        Optional<SysUser> userOptional = authRepository.findById(dto.getId());
        if (userOptional.isEmpty()) {
            return "error";
        }
        
        SysUser user = userOptional.get();
        
        // 更新用户信息
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        authRepository.save(user);
        
        return "用户信息更新成功";
    }

    @Override
    public String deleteUser(String id) {
        Optional<SysUser> userOptional = authRepository.findById(id);
        if (userOptional.isEmpty()) {
            return "error";
        }
        
        authRepository.deleteById(id);
        return "用户删除成功";
    }

    @Override
    public String updateUserStatus(String id, Integer status) {
        Optional<SysUser> userOptional = authRepository.findById(id);
        if (userOptional.isEmpty()) {
            return "error";
        }
        
        SysUser user = userOptional.get();
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        authRepository.save(user);
        
        return "用户状态更新成功";
    }
}
