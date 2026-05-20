package org.example.shoppingserver.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 消息幂等性校验工具类
 * 基于Redis实现消息去重
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis Key前缀：消息处理记录
     */
    private static final String IDEMPOTENT_KEY_PREFIX = "idempotent:message:";

    /**
     * 默认过期时间：7天（秒）
     */
    private static final long DEFAULT_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    /**
     * 检查并标记消息已处理（原子操作）
     * 
     * @param messageId 消息ID
     * @return true-首次处理，false-重复消息
     */
    public boolean tryProcess(String messageId) {
        return tryProcess(messageId, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 检查并标记消息已处理（原子操作）
     * 
     * @param messageId 消息ID
     * @param expireSeconds 过期时间（秒）
     * @return true-首次处理，false-重复消息
     */
    public boolean tryProcess(String messageId, long expireSeconds) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        
        // 使用setIfAbsent保证原子性：如果key不存在则设置，返回true；否则返回false
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
            key, 
            "processed", 
            expireSeconds, 
            TimeUnit.SECONDS
        );
        
        if (Boolean.TRUE.equals(success)) {
            log.debug("消息首次处理: messageId={}", messageId);
            return true;
        } else {
            log.warn("消息重复，已忽略: messageId={}", messageId);
            return false;
        }
    }

    /**
     * 检查消息是否已处理
     * 
     * @param messageId 消息ID
     * @return true-已处理，false-未处理
     */
    public boolean isProcessed(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 删除幂等性记录（用于测试或特殊场景）
     * 
     * @param messageId 消息ID
     */
    public void removeRecord(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.delete(key);
        log.info("删除幂等性记录: messageId={}", messageId);
    }
}
