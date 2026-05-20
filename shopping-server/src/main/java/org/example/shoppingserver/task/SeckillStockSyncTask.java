package org.example.shoppingserver.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.model.entity.marketing.SeckillActivity;
import org.example.shoppingserver.repository.SeckillActivityRepository;
import org.example.shoppingserver.util.SeckillStockUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 秒杀库存同步定时任务
 * 定期将Redis中的库存数据同步到数据库，保证数据一致性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillStockSyncTask {

    private final SeckillActivityRepository seckillActivityRepository;
    private final SeckillStockUtil seckillStockUtil;
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
     * 定时同步库存到数据库
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void syncStockToDatabase() {
        log.debug("开始执行秒杀库存同步任务");
        
        try {
            // 获取所有进行中的秒杀活动
            List<SeckillActivity> activities = seckillActivityRepository.findAll((root, query, cb) -> {
                return root.get("status").in(0, 1); // 未开始或进行中
            });

            if (activities == null || activities.isEmpty()) {
                log.debug("没有需要同步的秒杀活动");
                return;
            }

            int successCount = 0;
            int failCount = 0;
            int skipCount = 0;

            for (SeckillActivity activity : activities) {
                try {
                    boolean synced = syncActivityStock(activity);
                    if (synced) {
                        successCount++;
                    } else {
                        skipCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("同步活动库存失败: activityId={}, name={}, error={}", 
                            activity.getId(), activity.getName(), e.getMessage(), e);
                }
            }

            log.info("秒杀库存同步完成 - 总计: {}, 成功: {}, 跳过: {}, 失败: {}", 
                    activities.size(), successCount, skipCount, failCount);

        } catch (Exception e) {
            log.error("秒杀库存同步任务异常", e);
        }
    }

    /**
     * 同步单个活动的库存
     *
     * @param activity 秒杀活动
     * @return 是否同步成功
     */
    private boolean syncActivityStock(SeckillActivity activity) {
        Long activityId = activity.getId();
        
        // 从Redis获取库存和已售数量
        String stockKey = SECKILL_STOCK_KEY_PREFIX + activityId;
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        
        String redisStockStr = stringRedisTemplate.opsForValue().get(stockKey);
        String redisSoldStr = stringRedisTemplate.opsForValue().get(soldKey);
        
        // 如果Redis中没有数据，跳过
        if (redisStockStr == null || redisSoldStr == null) {
            log.debug("Redis中无库存数据，跳过同步: activityId={}", activityId);
            return false;
        }
        
        try {
            int redisStock = Integer.parseInt(redisStockStr);
            int redisSold = Integer.parseInt(redisSoldStr);
            
            // 获取数据库中的当前值
            int dbStock = activity.getStock();
            int dbSold = activity.getSoldCount();
            
            // 检查是否有变化
            if (redisStock == dbStock && redisSold == dbSold) {
                log.debug("库存无变化，跳过同步: activityId={}", activityId);
                return false;
            }
            
            // 验证数据合理性
            if (redisStock < 0 || redisSold < 0) {
                log.warn("Redis库存数据异常: activityId={}, stock={}, sold={}", 
                        activityId, redisStock, redisSold);
                return false;
            }
            
            // 同步到数据库
            activity.setStock(redisStock);
            activity.setSoldCount(redisSold);
            seckillActivityRepository.save(activity);
            
            log.info("库存同步成功: activityId={}, DB库存:{}→{}, DB已售:{}→{}", 
                    activityId, dbStock, redisStock, dbSold, redisSold);
            
            return true;
            
        } catch (NumberFormatException e) {
            log.error("Redis库存数据格式错误: activityId={}, stockStr={}, soldStr={}", 
                    activityId, redisStockStr, redisSoldStr, e);
            return false;
        }
    }

    /**
     * 定时校验库存一致性
     * 每5分钟执行一次
     * 检查Redis库存之和是否与活动总库存一致
     */
    @Scheduled(fixedRate = 300000)
    public void validateStockConsistency() {
        log.debug("开始执行库存一致性校验");
        
        try {
            List<SeckillActivity> activities = seckillActivityRepository.findAll((root, query, cb) -> {
                return root.get("status").in(0, 1);
            });

            if (activities == null || activities.isEmpty()) {
                return;
            }

            int inconsistentCount = 0;

            for (SeckillActivity activity : activities) {
                boolean consistent = validateActivityStock(activity);
                if (!consistent) {
                    inconsistentCount++;
                }
            }

            if (inconsistentCount > 0) {
                log.warn("发现 {} 个活动库存不一致，建议手动检查", inconsistentCount);
            } else {
                log.debug("所有活动库存一致");
            }

        } catch (Exception e) {
            log.error("库存一致性校验异常", e);
        }
    }

    /**
     * 校验单个活动的库存一致性
     *
     * @param activity 秒杀活动
     * @return 是否一致
     */
    private boolean validateActivityStock(SeckillActivity activity) {
        Long activityId = activity.getId();
        
        // 获取活动总库存
        String totalStockKey = SECKILL_STOCK_KEY_PREFIX + activityId;
        String totalStockStr = stringRedisTemplate.opsForValue().get(totalStockKey);
        
        if (totalStockStr == null) {
            return true; // Redis中无数据，无法校验
        }
        
        int redisTotalStock = Integer.parseInt(totalStockStr);
        int dbTotalStock = activity.getStock();
        
        // 检查活动级别库存是否一致
        if (redisTotalStock != dbTotalStock) {
            log.warn("活动库存不一致: activityId={}, Redis={}, DB={}", 
                    activityId, redisTotalStock, dbTotalStock);
            return false;
        }
        
        // 如果活动指定了SKU，检查SKU库存之和
        if (activity.getSkuId() != null) {
            String skuStockKey = "seckill:sku:stock:" + activityId + ":" + activity.getSkuId();
            String skuStockStr = stringRedisTemplate.opsForValue().get(skuStockKey);
            
            if (skuStockStr != null) {
                int skuStock = Integer.parseInt(skuStockStr);
                if (skuStock != redisTotalStock) {
                    log.warn("SKU库存与活动库存不一致: activityId={}, skuId={}, SKU库存={}, 活动库存={}", 
                            activityId, activity.getSkuId(), skuStock, redisTotalStock);
                    return false;
                }
            }
        } else {
            // 如果活动未指定SKU，检查所有SKU库存之和
            String pattern = "seckill:sku:stock:" + activityId + ":*";
            Set<String> skuKeys = stringRedisTemplate.keys(pattern);
            
            if (skuKeys != null && !skuKeys.isEmpty()) {
                int skuStockSum = 0;
                for (String key : skuKeys) {
                    String stockStr = stringRedisTemplate.opsForValue().get(key);
                    if (stockStr != null) {
                        skuStockSum += Integer.parseInt(stockStr);
                    }
                }
                
                if (skuStockSum != redisTotalStock) {
                    log.warn("SKU库存总和与活动库存不一致: activityId={}, SKU总和={}, 活动库存={}", 
                            activityId, skuStockSum, redisTotalStock);
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 手动触发同步（可通过Actuator或管理接口调用）
     */
    public void manualSync() {
        log.info("手动触发秒杀库存同步");
        syncStockToDatabase();
    }

    /**
     * 手动触发校验（可通过Actuator或管理接口调用）
     */
    public void manualValidate() {
        log.info("手动触发库存一致性校验");
        validateStockConsistency();
    }
}
