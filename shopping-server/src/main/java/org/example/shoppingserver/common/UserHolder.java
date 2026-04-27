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

    /**
     * 获取当前商家ID
     *
     * @return 商家ID
     */
    public static Long getCurrentMerchantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 从认证信息的details或principal中获取商家ID
            // 这里假设在JWT或其他地方存储了商家ID
            Object details = authentication.getDetails();
            if (details instanceof java.util.Map) {
                Object merchantId = ((java.util.Map<?, ?>) details).get("merchantId");
                if (merchantId != null) {
                    return Long.valueOf(merchantId.toString());
                }
            }
        }
        return null;
    }
}
