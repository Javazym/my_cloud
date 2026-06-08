package org.example.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * 配置连接和读取超时时间
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时时间：10秒
        ((SimpleClientHttpRequestFactory) factory).setConnectTimeout(10000);
        // 读取超时时间：30秒
        ((SimpleClientHttpRequestFactory) factory).setReadTimeout(30000);
        
        return new RestTemplate(factory);
    }
}
