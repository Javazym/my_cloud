package org.example.authserver.util.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.UUID;

@Configuration
public class JwkConfig {

    // 定义一个唯一的 Key ID，Token 的 header 里也会带上这个 ID
    private static final String KID = "QLBM2905";

    @Bean
    public JWKSet jwkSet() throws Exception {
        // 1. 读取公钥文件 (src/main/resources/public.pem)
        ClassPathResource resource = new ClassPathResource("public.pem");
        String publicKeyContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // 2. 清理 PEM 格式头尾，提取 Base64 部分
        String content = publicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 去除换行和空格

        // 3. 转换为 Java RSAPublicKey 对象
        byte[] decoded = java.util.Base64.getDecoder().decode(content);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) kf.generatePublic(spec);

        // 4. 构建 Nimbus RSAKey
        RSAKey jwk = new RSAKey.Builder(rsaPublicKey)
                .keyID(KID)       // 设置 kid
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256) // 设置算法
                .build();

        // 5. 构建 JWKSet (即使只有一个 key，也必须是列表形式)
        return new JWKSet(Collections.singletonList(jwk));
    }
}
