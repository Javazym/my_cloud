package org.example.authserver.service;

import org.example.authserver.model.dto.create.AdminCreateDto;
import org.example.authserver.model.dto.create.RegisterDto;
import org.example.authserver.model.dto.query.AuthCodeDto;
import org.example.authserver.model.dto.query.LoginDto;
import org.example.authserver.model.dto.query.UserQueryDto;
import org.example.authserver.model.dto.update.UserUpdateDto;
import org.example.authserver.model.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String login(LoginDto dto);
    String register(RegisterDto dto);
    String authCode(AuthCodeDto dto);
    
    // 管理员相关方法
    String createAdmin(AdminCreateDto dto);
    
    // 用户管理相关方法
    Page<SysUser> getUsers(UserQueryDto queryDto);
    SysUser getUserById(String id);
    String updateUser(UserUpdateDto dto);
    String deleteUser(String id);
    String updateUserStatus(String id, Integer status);
}
