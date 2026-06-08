package org.example.commonapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /**
     * 配置 ObjectMapper 处理 Java 8 时间格式
     * 解决 Feign 序列化/反序列化 LocalDateTime 的问题
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 注册 Java 8 时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 定义日期格式
        DateTimeFormatter parserFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter serializerFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // 添加反序列化器（接收的格式）
        javaTimeModule.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(parserFormatter));
        
        // 添加序列化器（输出的格式）
        javaTimeModule.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(serializerFormatter));
        
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
