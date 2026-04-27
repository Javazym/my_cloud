package org.example.mygateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:my-super-secret-key-for-gateway-auth-2026}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    private SecretKey getSigningKey() {
        // 确保密钥长度足够 (HS256 需要至少 256 bit)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 解析并验证 Token
     * @return Claims 如果有效，否则抛出异常
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期", e);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token 非法", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Token 签名验证失败", e);
        }
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
