package org.example.shoppingserver.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * 用户名
     */
    @Id
    private String id;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 255)
    private String avatar;


    /**
     * 邮箱
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 生日
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status")
    private Integer status = 1;


    /**
     * 账户余额
     */
    @Column(name = "balance", precision = 10, scale = 2)
    private java.math.BigDecimal balance = java.math.BigDecimal.ZERO;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 收货地址列表
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    /**
     * 收藏列表
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favorite> favorites = new ArrayList<>();

    /**
     * 添加收货地址
     */
    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.setUser(this);
    }

    /**
     * 移除收货地址
     */
    public void removeAddress(UserAddress address) {
        addresses.remove(address);
        address.setUser(null);
    }
}
