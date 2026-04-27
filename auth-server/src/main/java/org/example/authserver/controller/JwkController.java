package org.example.authserver.controller;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwkController {

    private final JWKSet jwkSet;

    // 注入上面配置的 JWKSet
    public JwkController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    /**
     * 暴露 JWK Set 端点
     * URL: /.well-known/jwks.json
     * 注意：路径必须严格匹配，Spring Security 有时会对 .well-known 有特殊处理，
     * 如果冲突，可以改为 /oauth2/jwks.json 并在网关配置对应地址
     */
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwk() {
        return jwkSet.toJSONObject();
    }
}
