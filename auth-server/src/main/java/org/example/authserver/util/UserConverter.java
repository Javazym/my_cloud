package org.example.authserver.util;

import org.example.authserver.entity.Role;
import org.example.authserver.entity.SysUser;
import org.example.authserver.vo.UserVO;

import java.util.stream.Collectors;

public class UserConverter {
    
    public static UserVO convertToVO(SysUser user) {
        if (user == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        
        // 转换角色名称列表
        if (user.getRoles() != null) {
            vo.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        }
        
        return vo;
    }
}