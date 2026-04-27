package org.example.authserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    // BCrypt 强度因子 (4-31)，默认 10。越大越安全但越慢。
    private static final int STRENGTH = 10;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);

    /**
     * 加密密码 (注册/修改密码时使用)
     * @param rawPassword 明文密码
     * @return 密文 (例如: $2a$10$N.zmdr9k7uOCQb376NoUnu...)
     */
    public String encode(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码 (登录时使用)
     * @param rawPassword 用户输入的明文密码
     * @param encodedPassword 数据库中存储的密文
     * @return true 如果匹配，false 如果不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}
