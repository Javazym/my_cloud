package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员Repository
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 根据用户名查询管理员
     */
    Optional<Admin> findByUsername(String username);


    /**
     * 根据状态查询管理员列表
     */
    java.util.List<Admin> findByStatus(Integer status);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
}
