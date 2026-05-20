package org.example.shoppingserver.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀库存工具类
 * 提供Redis中秒杀库存的操作方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillStockUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis Key前缀：秒杀活动库存
     */
    private static final String SECKILL_STOCK_KEY_PREFIX = "seckill:stock:";

    /**
     * Redis Key前缀：秒杀活动已售数量
     */
    private static final String SECKILL_SOLD_KEY_PREFIX = "seckill:sold:";

    /**
     * Redis Key前缀：秒杀活动SKU库存
     */
    private static final String SECKILL_SKU_STOCK_KEY_PREFIX = "seckill:sku:stock:";

    /**
     * Redis Key前缀：秒杀活动SKU已售数量
     */
    private static final String SECKILL_SKU_SOLD_KEY_PREFIX = "seckill:sku:sold:";

    /**
     * 获取秒杀活动库存
     *
     * @param activityId 活动ID
     * @return 库存数量，如果不存在返回-1
     */
    public int getStock(Long activityId) {
        String key = SECKILL_STOCK_KEY_PREFIX + activityId;
        String stock = stringRedisTemplate.opsForValue().get(key);
        
        if (stock == null) {
            log.warn("Redis中未找到活动库存: activityId={}", activityId);
            return -1;
        }
        
        try {
            return Integer.parseInt(stock);
        } catch (NumberFormatException e) {
            log.error("库存格式错误: activityId={}, stock={}", activityId, stock);
            return -1;
        }
    }

    /**
     * 获取秒杀活动已售数量
     *
     * @param activityId 活动ID
     * @return 已售数量，如果不存在返回0
     */
    public int getSoldCount(Long activityId) {
        String key = SECKILL_SOLD_KEY_PREFIX + activityId;
        String soldCount = stringRedisTemplate.opsForValue().get(key);
        
        if (soldCount == null) {
            return 0;
        }
        
        try {
            return Integer.parseInt(soldCount);
        } catch (NumberFormatException e) {
            log.error("已售数量格式错误: activityId={}, soldCount={}", activityId, soldCount);
            return 0;
        }
    }

    /**
     * 扣减库存（原子操作）
     * 使用Redis的decr命令保证原子性
     *
     * @param activityId 活动ID
     * @return 是否扣减成功
     */
    public boolean decreaseStock(Long activityId) {
        String stockKey = SECKILL_STOCK_KEY_PREFIX + activityId;
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        
        // 先检查库存是否充足
        int currentStock = getStock(activityId);
        if (currentStock <= 0) {
            log.warn("库存不足: activityId={}, stock={}", activityId, currentStock);
            return false;
        }
        
        // 原子性扣减库存
        Long newStock = stringRedisTemplate.opsForValue().decrement(stockKey);
        if (newStock == null || newStock < 0) {
            // 如果扣减后库存小于0，需要回滚
            if (newStock != null && newStock < 0) {
                stringRedisTemplate.opsForValue().increment(stockKey);
            }
            log.warn("库存扣减失败: activityId={}, newStock={}", activityId, newStock);
            return false;
        }
        
        // 增加已售数量
        stringRedisTemplate.opsForValue().increment(soldKey);
        
        log.debug("库存扣减成功: activityId={}, 剩余库存={}", activityId, newStock);
        return true;
    }

    /**
     * 恢复库存（取消订单时使用）
     *
     * @param activityId 活动ID
     * @return 是否恢复成功
     */
    public boolean restoreStock(Long activityId) {
        String stockKey = SECKILL_STOCK_KEY_PREFIX + activityId;
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        
        // 增加库存
        stringRedisTemplate.opsForValue().increment(stockKey);
        
        // 减少已售数量
        Long newSoldCount = stringRedisTemplate.opsForValue().decrement(soldKey);
        if (newSoldCount != null && newSoldCount < 0) {
            // 如果已售数量小于0，需要回滚
            stringRedisTemplate.opsForValue().increment(soldKey);
            stringRedisTemplate.opsForValue().decrement(stockKey);
            log.error("恢复库存失败，已售数量异常: activityId={}, soldCount={}", activityId, newSoldCount);
            return false;
        }
        
        log.debug("库存恢复成功: activityId={}", activityId);
        return true;
    }

    /**
     * 检查是否有库存
     *
     * @param activityId 活动ID
     * @return 是否有库存
     */
    public boolean hasStock(Long activityId) {
        return getStock(activityId) > 0;
    }

    /**
     * 设置库存（用于手动调整或初始化）
     *
     * @param activityId 活动ID
     * @param stock 库存数量
     * @param expireSeconds 过期时间（秒）
     */
    public void setStock(Long activityId, int stock, long expireSeconds) {
        String key = SECKILL_STOCK_KEY_PREFIX + activityId;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(stock), expireSeconds, TimeUnit.SECONDS);
        log.info("设置库存: activityId={}, stock={}, expireSeconds={}", activityId, stock, expireSeconds);
    }

    /**
     * 设置已售数量（用于手动调整或初始化）
     *
     * @param activityId 活动ID
     * @param soldCount 已售数量
     * @param expireSeconds 过期时间（秒）
     */
    public void setSoldCount(Long activityId, int soldCount, long expireSeconds) {
        String key = SECKILL_SOLD_KEY_PREFIX + activityId;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(soldCount), expireSeconds, TimeUnit.SECONDS);
        log.info("设置已售数量: activityId={}, soldCount={}, expireSeconds={}", activityId, soldCount, expireSeconds);
    }

    /**
     * 删除活动库存信息
     *
     * @param activityId 活动ID
     */
    public void deleteStockInfo(Long activityId) {
        String stockKey = SECKILL_STOCK_KEY_PREFIX + activityId;
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        
        stringRedisTemplate.delete(stockKey);
        stringRedisTemplate.delete(soldKey);
        
        log.info("删除活动库存信息: activityId={}", activityId);
    }

    // ==================== SKU级别的库存操作 ====================

    /**
     * 获取SKU库存
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @return 库存数量，如果不存在返回-1
     */
    public int getSkuStock(Long activityId, Long skuId) {
        String key = SECKILL_SKU_STOCK_KEY_PREFIX + activityId + ":" + skuId;
        String stock = stringRedisTemplate.opsForValue().get(key);
        
        if (stock == null) {
            log.warn("Redis中未找到SKU库存: activityId={}, skuId={}", activityId, skuId);
            return -1;
        }
        
        try {
            return Integer.parseInt(stock);
        } catch (NumberFormatException e) {
            log.error("SKU库存格式错误: activityId={}, skuId={}, stock={}", activityId, skuId, stock);
            return -1;
        }
    }

    /**
     * 获取SKU已售数量
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @return 已售数量，如果不存在返回0
     */
    public int getSkuSoldCount(Long activityId, Long skuId) {
        String key = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + skuId;
        String soldCount = stringRedisTemplate.opsForValue().get(key);
        
        if (soldCount == null) {
            return 0;
        }
        
        try {
            return Integer.parseInt(soldCount);
        } catch (NumberFormatException e) {
            log.error("SKU已售数量格式错误: activityId={}, skuId={}, soldCount={}", 
                    activityId, skuId, soldCount);
            return 0;
        }
    }

    /**
     * 扣减SKU库存（原子操作）
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @return 是否扣减成功
     */
    public boolean decreaseSkuStock(Long activityId, Long skuId) {
        String stockKey = SECKILL_SKU_STOCK_KEY_PREFIX + activityId + ":" + skuId;
        String soldKey = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + skuId;
        
        // 先检查库存是否充足
        int currentStock = getSkuStock(activityId, skuId);
        if (currentStock <= 0) {
            log.warn("SKU库存不足: activityId={}, skuId={}, stock={}", activityId, skuId, currentStock);
            return false;
        }
        
        // 原子性扣减库存
        Long newStock = stringRedisTemplate.opsForValue().decrement(stockKey);
        if (newStock == null || newStock < 0) {
            // 如果扣减后库存小于0，需要回滚
            if (newStock != null && newStock < 0) {
                stringRedisTemplate.opsForValue().increment(stockKey);
            }
            log.warn("SKU库存扣减失败: activityId={}, skuId={}, newStock={}", 
                    activityId, skuId, newStock);
            return false;
        }
        
        // 增加已售数量
        stringRedisTemplate.opsForValue().increment(soldKey);
        
        log.debug("SKU库存扣减成功: activityId={}, skuId={}, 剩余库存={}", 
                activityId, skuId, newStock);
        return true;
    }

    /**
     * 恢复SKU库存（取消订单时使用）
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @return 是否恢复成功
     */
    public boolean restoreSkuStock(Long activityId, Long skuId) {
        String stockKey = SECKILL_SKU_STOCK_KEY_PREFIX + activityId + ":" + skuId;
        String soldKey = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + skuId;
        
        // 增加库存
        stringRedisTemplate.opsForValue().increment(stockKey);
        
        // 减少已售数量
        Long newSoldCount = stringRedisTemplate.opsForValue().decrement(soldKey);
        if (newSoldCount != null && newSoldCount < 0) {
            // 如果已售数量小于0，需要回滚
            stringRedisTemplate.opsForValue().increment(soldKey);
            stringRedisTemplate.opsForValue().decrement(stockKey);
            log.error("恢复SKU库存失败，已售数量异常: activityId={}, skuId={}, soldCount={}", 
                    activityId, skuId, newSoldCount);
            return false;
        }
        
        log.debug("SKU库存恢复成功: activityId={}, skuId={}", activityId, skuId);
        return true;
    }

    /**
     * 检查SKU是否有库存
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @return 是否有库存
     */
    public boolean hasSkuStock(Long activityId, Long skuId) {
        return getSkuStock(activityId, skuId) > 0;
    }
}
