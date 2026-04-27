package org.example.mygateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 1. 关闭 CSRF (网关通常是无状态的 API，不需要 CSRF)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. 配置授权规则
                .authorizeExchange(exchanges -> exchanges
                        // 白名单：登录、注册、公开接口、文档
                        .pathMatchers("/auth/**", "/public/**", "/actuator/**").permitAll()
                        // 其他所有请求必须认证
                        .anyExchange().authenticated()
                )

                // 3. 配置 OAuth2 Resource Server (JWT 模式)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // 自定义 JWT 转换器，提取角色
                                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(
                                        costomjwtAuthenticationConverter()))
                        )
                )
                // 4. 配置 CORS (解决跨域问题，关键！)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    /**
     * 自定义 JWT 转换器
     * 默认只读取 scope/audience，我们需要读取 custom claim (如 roles, userId)
     */
    @Bean
    public JwtAuthenticationConverter costomjwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // 设置角色提取逻辑 (假设 JWT 中有一个 "roles" 字段，值为 ["ADMIN", "USER"])
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // 角色前缀
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // JWT 中的字段名

        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    // 简单的 CORS 配置源 (也可以单独抽离成一个 Bean)
    private org.springframework.web.cors.reactive.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 生产环境请指定具体域名
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}