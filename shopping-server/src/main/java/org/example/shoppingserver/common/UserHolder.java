package org.example.shoppingserver.common;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户上下文持有者
 * 用于获取当前登录用户信息
 */
public class UserHolder {

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前用户认证信息
     *
     * @return 认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
