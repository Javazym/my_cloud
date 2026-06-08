package org.example.commonapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonTimeConfig {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule timeModule = new JavaTimeModule();

        // 注册中文日期格式的支持
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(formatter));
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(formatter));

        objectMapper.registerModule(timeModule);

        return objectMapper;
    }
}