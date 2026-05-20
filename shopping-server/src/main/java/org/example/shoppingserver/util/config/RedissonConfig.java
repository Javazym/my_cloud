package org.example.shoppingserver.util.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 * 用于分布式锁等高级Redis功能
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 单机模式配置
        String address = "redis://" + redisHost + ":" + redisPort;
        log.info("Redisson连接地址: {}", address);
        
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisDatabase)
                .setConnectionPoolSize(64)  // 连接池大小
                .setConnectionMinimumIdleSize(24)  // 最小空闲连接数
                .setIdleConnectionTimeout(10000)  // 空闲连接超时时间
                .setConnectTimeout(10000)  // 连接超时时间
                .setTimeout(3000)  // 命令等待超时时间
                .setRetryAttempts(3)  // 命令失败重试次数
                .setRetryInterval(1500);  // 命令重试间隔时间
        
        // 如果有密码，设置密码
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.useSingleServer().setPassword(redisPassword);
        }
        
        log.info("Redisson客户端初始化成功");
        return Redisson.create(config);
    }
}
