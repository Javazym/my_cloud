package org.example.shoppingserver.model.vo.admin;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员VO
 */
@Data
public class AdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
