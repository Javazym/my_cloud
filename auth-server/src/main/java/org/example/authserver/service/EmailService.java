package org.example.authserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    // 构造函数注入
    public EmailService(JavaMailSender mailSender, StringRedisTemplate redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送验证码邮件
     * @param toEmail 接收者邮箱
     * @return 是否发送成功 (实际项目中建议返回 void，异常由全局处理器捕获)
     */
    public boolean sendVerificationCode(String toEmail) {
        // 1. 生成 6 位随机验证码
        String code = generateCode();

        // 2. 定义 Redis Key (格式：login_code:邮箱)
        String redisKey = "login_code:" + toEmail;

        // 3. 存入 Redis，设置过期时间 5 分钟 (300 秒)
        // 如果 key 已存在，覆盖它（防止频繁点击），实际业务可加限流逻辑
        redisTemplate.opsForValue().set(redisKey, code, 10, TimeUnit.MINUTES);

        // 4. 构建邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("zymwppt@qq.com"); // 必须与配置文件中的 username 一致
        message.setTo(toEmail);
        message.setSubject("【星悦个人工作室】您的登录验证码");

        // 邮件正文
        String content = "尊敬的用户：\n\n" +
                "您的登录验证码为：" + code + "\n\n" +
                "该验证码 10 分钟内有效，请勿泄露给他人。\n" +
                "如果不是您本人操作，请忽略此邮件。";
        message.setText(content);

        try {
            // 5. 发送邮件
            mailSender.send(message);
            System.out.println("验证码邮件已发送至：" + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("邮件发送失败：" + e.getMessage());
            // 生产环境建议记录日志 log.error(...)
            throw new RuntimeException("邮件发送失败，请稍后重试", e);
        }
    }

    /**
     * 校验验证码
     * @param email 邮箱
     * @param inputCode 用户输入的验证码
     * @return 校验结果
     */
    public boolean verifyCode(String email, String inputCode) {
        String redisKey = "login_code:" + email;
        String realCode = redisTemplate.opsForValue().get(redisKey);
        log.info(realCode);
        if (realCode == null) {
            log.info("验证码不存在或已过期");
            return false; // 验证码过期或不存在
        }

        if (realCode.equalsIgnoreCase(inputCode)) {
            // 校验成功后，立即删除 Redis 中的验证码，防止重用
            redisTemplate.delete(redisKey);
            return true;
        }

        return false;
    }

    // 生成 6 位数字验证码
    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
