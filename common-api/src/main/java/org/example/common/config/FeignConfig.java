package org.example.common.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 配置类
 * 提供日志级别和错误处理配置
 */
@Slf4j
@Configuration
public class FeignConfig {

    /**
     * 配置 Feign 日志级别
     * BASIC: 只记录请求方法和 URL 以及响应状态代码和执行时间
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 自定义错误解码器
     * 用于处理 Feign 调用失败时的异常
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * 自定义错误解码器实现
     */
    static class CustomErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            if (response.status() >= 500) {
                log.error("Feign 服务调用失败: method={}, status={}, reason={}", 
                        methodKey, response.status(), response.reason());
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
