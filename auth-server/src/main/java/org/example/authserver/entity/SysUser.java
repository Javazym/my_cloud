package org.example.authserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_user")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"roles"})
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    private int status;
    private LocalDateTime createTime;
    @ManyToMany
    @JoinTable(
            name = "user_role", // 中间表名
            joinColumns = @JoinColumn(name = "user_id"), // 当前实体外键
            inverseJoinColumns = @JoinColumn(name = "role_id") // 关联表外键
    )
    private Set<Role> roles = new HashSet<>();

}
