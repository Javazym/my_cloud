package org.example.mygateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AddUserHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AddUserHeaderFilter: filter");
        Mono<Jwt> jwtMono = ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    var authentication = context.getAuthentication();
                    if (authentication instanceof JwtAuthenticationToken) {
                        return Mono.just(((JwtAuthenticationToken) authentication).getToken());
                    }
                    return Mono.empty();
                });

        return jwtMono
                .flatMap(jwt -> {
                    // 1. 获取用户信息
                    String userId = jwt.getSubject();
                    String username = jwt.getClaimAsString("username");

                    // 2. 【关键】获取角色并传递
                    List<String> roles = jwt.getClaimAsStringList("roles");

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId != null ? userId : "unknown")
                            .header("X-Username", username != null ? username : "unknown")
                            .header("X-User-Role", String.join(",", roles)) // 传递角色
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}