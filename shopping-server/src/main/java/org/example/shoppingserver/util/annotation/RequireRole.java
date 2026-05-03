package org.example.shoppingserver.util.annotation;

import java.lang.annotation.*;

/**
 * 角色校验注解
 * 用于标记需要特定角色才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     * 例如：{"ADMIN", "MERCHANT"}
     */
    String[] value();
}
