package org.example.shoppingserver.util.annotation;

import java.lang.annotation.*;

/**
 * 忽略认证注解
 * 用于标记不需要登录即可访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
}
