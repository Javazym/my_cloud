package org.example.shoppingserver.model.entity.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admins")
public class Admin extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
}
