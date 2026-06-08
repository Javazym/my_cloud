package org.example.agentserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

/**
 * 全局 Feign 配置
 * 为 agent-server 中的所有 Feign Client 提供统一的认证头和解码器配置
 */
@Configuration
public class GlobalFeignConfig {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 提供带日期格式的 ObjectMapper Bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern(DATE_PATTERN);
        
        javaTimeModule.addDeserializer(
            java.time.LocalDateTime.class,
            new LocalDateTimeDeserializer(formatter)
        );
        javaTimeModule.addSerializer(
            java.time.LocalDateTime.class,
            new LocalDateTimeSerializer(formatter)
        );
        
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("X-User-Id", "1");
                template.header("X-User-Name", "admin");
                template.header("X-User-Role", "ADMIN");
            }
        };
    }

    /**
     * 使用自定义 ObjectMapper 的 Decoder，支持 Page 对象
     */
    @Bean
    public Decoder feignDecoder() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // 忽略未知字段（解决 httpStatus 问题）
            mapper.configure(
                com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
                false
            );
            
            // 添加 Java Time 模块
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            java.time.format.DateTimeFormatter formatter = 
                java.time.format.DateTimeFormatter.ofPattern(DATE_PATTERN);
            javaTimeModule.addDeserializer(
                java.time.LocalDateTime.class,
                new LocalDateTimeDeserializer(formatter)
            );
            javaTimeModule.addSerializer(
                java.time.LocalDateTime.class,
                new LocalDateTimeSerializer(formatter)
            );
            mapper.registerModule(javaTimeModule);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // 注册 Page 的反序列化器（解决 Page 反序列化问题）
            SimpleModule pageModule = new SimpleModule("PageModule");
            pageModule.addDeserializer(org.springframework.data.domain.Page.class, 
                new StdDeserializer<org.springframework.data.domain.Page>(org.springframework.data.domain.Page.class) {
                    @Override
                    public org.springframework.data.domain.Page deserialize(com.fasterxml.jackson.core.JsonParser p, 
                                                                                            com.fasterxml.jackson.databind.DeserializationContext ctxt) 
                            throws IOException {
                        ObjectMapper om = (ObjectMapper) p.getCodec();
                        com.fasterxml.jackson.databind.JsonNode node = om.readTree(p);
                        
                        // 提取 content 字段
                        com.fasterxml.jackson.databind.JsonNode contentNode = node.get("content");
                        java.util.List<Object> content = om.convertValue(contentNode, 
                            om.getTypeFactory().constructCollectionType(java.util.List.class, Object.class));
                        
                        // 提取分页信息（Spring Data 使用的是 number 和 size）
                        long totalElements = node.get("totalElements").asLong();
                        int pageNumber = node.get("number").asInt();
                        int pageSize = node.get("size").asInt();
                        
                        // 使用 PageImpl 构造 Page 对象
                        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
                    }
                });
            
            mapper.registerModule(pageModule);
            
            MappingJackson2HttpMessageConverter converter = 
                new MappingJackson2HttpMessageConverter(mapper);
            HttpMessageConverters convertors = new HttpMessageConverters(converter);
            ObjectFactory<HttpMessageConverters> factory = () -> convertors;
            
            return new SpringDecoder(factory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Feign Decoder", e);
        }
    }

    @Bean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringEncoder(messageConverters);
    }
}
