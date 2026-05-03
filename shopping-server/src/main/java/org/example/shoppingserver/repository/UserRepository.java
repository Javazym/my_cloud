package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);


    /**
     * 根据邮箱查询用户
     */
    Optional<User> findByEmail(String email);


    /**
     * 查询用户总数
     */
    long countByStatus(Integer status);

    /**
     * 根据状态查询用户列表
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
}
