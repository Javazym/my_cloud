package org.example.shoppingserver.util.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
@Slf4j
@Component
@Order(1)
public class UserAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 从请求头获取（网关传过来）
        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");
        String username = request.getHeader("X-User-Name");
        log.info("我来了");
        if (userId == null || userRole == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 封装角色信息（SpringSecurity规范：必须加 ROLE_ 前缀）
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userRole)
        );

        // 构建用户信息
        UserDetails userDetails = User.withUsername(userId)
                .password("")
                .authorities(authorities)
                .build();

        // 放入安全上下文
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}