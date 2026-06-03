package org.example.shoppingserver.util.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.util.annotation.IgnoreAuth;
import org.example.shoppingserver.util.annotation.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
public class UserAuthFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 获取当前请求的处理器方法
        HandlerMethod handlerMethod = getHandlerMethod(request);
        
        // 检查是否有 @IgnoreAuth 注解（白名单）
        if (hasIgnoreAuthAnnotation(handlerMethod)) {
            log.debug("请求在白名单中，跳过认证: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头获取（网关传过来）
        String userId = request.getHeader("X-User-Id");
        String userRoles = request.getHeader("X-User-Role");  // 可能是多个角色，用逗号分隔
        String username = request.getHeader("X-User-Name");
        
        log.info("用户认证 - UserId: {}, Roles: {}, Username: {}", userId, userRoles, username);
        
        if (userId == null || userRoles == null) {
            log.warn("未找到用户信息，拒绝访问: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":3000,\"message\":\"未登录或登录已过期\"}");
            return;
        }

        // 解析角色列表（支持多个角色，用逗号分隔）
        List<String> roleList = Arrays.asList(userRoles.split(","));
        
        // 封装角色信息（SpringSecurity规范：必须加 ROLE_ 前缀）
        List<GrantedAuthority> authorities = roleList.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .collect(Collectors.toList());

        // 构建用户信息
        UserDetails userDetails = User.withUsername(userId)
                .password("")
                .authorities(authorities)
                .build();

        // 放入安全上下文
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        // 检查角色权限（如果有 @RequireRole 注解）
        if (!checkRolePermission(handlerMethod, roleList)) {
            log.warn("用户角色不足，拒绝访问: {}, 用户角色: {}", request.getRequestURI(), userRoles);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":3011,\"message\":\"权限不足，无法执行此操作\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * 获取请求对应的处理器方法
     */
    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            Object handler = handlerMapping.getHandler(request).getHandler();
            if (handler instanceof HandlerMethod) {
                return (HandlerMethod) handler;
            }
        } catch (Exception e) {
            log.error("获取处理器方法失败", e);
        }
        return null;
    }
    
    /**
     * 检查是否有 @IgnoreAuth 注解
     */
    private boolean hasIgnoreAuthAnnotation(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return false;
        }
        
        // 检查方法级别的注解
        if (handlerMethod.hasMethodAnnotation(IgnoreAuth.class)) {
            return true;
        }
        
        // 检查类级别的注解
        return handlerMethod.getBeanType().isAnnotationPresent(IgnoreAuth.class);
    }
    
    /**
     * 检查角色权限
     */
    private boolean checkRolePermission(HandlerMethod handlerMethod, List<String> userRoles) {
        if (handlerMethod == null) {
            return true; // 没有处理器信息，默认允许
        }
        
        RequireRole requireRole = null;
        
        // 优先检查方法级别的注解
        if (handlerMethod.hasMethodAnnotation(RequireRole.class)) {
            requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        } 
        // 其次检查类级别的注解
        else if (handlerMethod.getBeanType().isAnnotationPresent(RequireRole.class)) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        
        // 如果没有 @RequireRole 注解，默认允许访问
        if (requireRole == null) {
            return true;
        }
        
        // 检查用户角色是否在允许的角色列表中
        String[] allowedRoles = requireRole.value();
        List<String> allowedRoleList = Arrays.asList(allowedRoles);
        
        // 只要用户的任意一个角色在允许列表中，就通过验证
        boolean hasPermission = userRoles.stream()
                .map(String::trim)
                .anyMatch(allowedRoleList::contains);
        
        log.debug("角色校验 - 用户角色: {}, 允许的角色: {}, 结果: {}", 
                  userRoles, Arrays.toString(allowedRoles), hasPermission);
        
        return hasPermission;
    }
}