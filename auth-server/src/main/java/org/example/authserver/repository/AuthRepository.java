package org.example.authserver.repository;

import org.example.authserver.model.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<SysUser, String> {
    SysUser findByUsername(String username);
    SysUser findByEmail(String email);
    
    // 用于分页查询所有用户
    org.springframework.data.domain.Page<SysUser> findAll(org.springframework.data.domain.Pageable pageable);
}
