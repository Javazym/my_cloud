package org.example.shoppingserver.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名/手机号/邮箱
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
