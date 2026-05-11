package org.example.shoppingserver.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册DTO
 */
@Data
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;
}
