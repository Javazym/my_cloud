package org.example.authserver.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.example.authserver.entity.Role;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Component
public class JwtGenerator {

    private final RSAPrivateKey privateKey;
    private final String keyId = "QLBM2905"; // 必须与 JWK 配置中的 KID 一致

    public JwtGenerator() throws Exception {
        // 1. 加载私钥文件 (src/main/resources/private.pem)
        // ⚠️ 生产环境建议从环境变量或配置中心加载，不要硬编码在 classpath
        ClassPathResource resource = new ClassPathResource("private.pem");
        String privateKeyContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // 2. 清理 PEM 格式头尾
        String content = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // 3. 转换为 Java RSAPrivateKey 对象
        byte[] decoded = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户唯一标识 (sub)
     * @param username 用户名 (自定义 claim)
     * @param roles    角色列表 (自定义 claim)
     * @return JWT 字符串
     */
    public String generateToken(String userId, String username, Set<Role> roles) throws Exception {
        // 1. 设置有效期 (例如 1 小时)
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600 * 100000);

        // 2. 构建 Claims (载荷)
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)                // sub: 主题 (用户 ID)
                                                // iss: 签发者 (需与网关配置的 issuer 匹配，如果网关校验 issuer)
                .issueTime(now)                 // iat: 签发时间
                .expirationTime(expiryDate)     // exp: 过期时间
                .jwtID(UUID.randomUUID().toString()) // jti: 唯一 ID (防重放)
                .claim("username", username)    // 自定义字段：用户名
                .claim("roles", Role.getRoleNames(roles))          // 自定义字段：角色列表
                .build();

        // 3. 构建 Header (头部)
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(keyId) // ⚠️ 关键：这里的 kid 必须与 JWK 接口返回的 kid 一致
                .build();

        // 4. 创建 SignedJWT 并签名
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        RSASSASigner signer = new RSASSASigner(privateKey);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
