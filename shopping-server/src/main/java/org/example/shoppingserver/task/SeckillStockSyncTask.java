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
     * Redis Key前缀：秒杀SKU库存
     */
    private static final String SECKILL_SKU_STOCK_KEY_PREFIX = "seckill:sku:stock:";

    /**
     * Redis Key前缀：秒杀SKU已售数量
     */
    private static final String SECKILL_SKU_SOLD_KEY_PREFIX = "seckill:sku:sold:";

    /**
     * 定时同步已售数量到数据库
     * 每分钟执行一次
     * 注意：只同步已售数量的增量，库存总量不变
     */
    @Scheduled(fixedRate = 60000)
    public void syncStockToDatabase() {
        log.debug("开始执行秒杀库存同步任务");
        
        try {
            // 获取所有进行中的秒杀活动
            List<SeckillActivity> activities = seckillActivityRepository.findByStatusIn(List.of(0, 1));

            if (activities == null || activities.isEmpty()) {
                log.debug("没有需要同步的秒杀活动");
                return;
            }

            int successCount = 0;
            int failCount = 0;
            int skipCount = 0;

            for (SeckillActivity activity : activities) {
                try {
                    boolean synced = syncActivitySoldCount(activity);
                    if (synced) {
                        successCount++;
                    } else {
                        skipCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("同步活动已售数量失败: activityId={}, name={}, error={}", 
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
     * 同步单个活动的已售数量
     * 逻辑：从Redis获取已售数量，更新到数据库
     *
     * @param activity 秒杀活动
     * @return 是否同步成功
     */
    private boolean syncActivitySoldCount(SeckillActivity activity) {
        Long activityId = activity.getId();
        
        // 从Redis获取已售数量
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        String redisSoldStr = stringRedisTemplate.opsForValue().get(soldKey);
        
        // 如果Redis中没有数据，跳过
        if (redisSoldStr == null) {
            log.debug("Redis中无已售数据，跳过同步: activityId={}", activityId);
            return false;
        }
        
        try {
            int redisSold = Integer.parseInt(redisSoldStr);
            
            // 获取数据库中的当前值
            int dbSold = activity.getSoldCount();
            
            // 检查是否有变化
            if (redisSold == dbSold) {
                log.debug("已售数量无变化，跳过同步: activityId={}", activityId);
                return false;
            }
            
            // 验证数据合理性
            if (redisSold < 0) {
                log.warn("Redis已售数据异常: activityId={}, sold={}", activityId, redisSold);
                return false;
            }
            
            // 同步到数据库：只更新已售数量，库存总量不变
            // 实际库存 = 初始总库存 - 已售数量
            activity.setSoldCount(redisSold);
            activity.setStock(activity.getStock()); // 保持初始库存不变
            seckillActivityRepository.save(activity);
            
            log.info("已售数量同步成功: activityId={}, DB已售:{}→{}, 当前库存:{}", 
                    activityId, dbSold, redisSold, activity.getStock() - redisSold);
            
            return true;
            
        } catch (NumberFormatException e) {
            log.error("Redis已售数据格式错误: activityId={}, soldStr={}", 
                    activityId, redisSoldStr, e);
            return false;
        }
    }

    /**
     * 同步SKU级别的已售数量
     */
    @Scheduled(fixedRate = 60000)
    public void syncSkuStockToDatabase() {
        log.debug("开始执行SKU库存同步任务");
        
        try {
            // 查找所有带SKU的秒杀活动
            List<SeckillActivity> activities = seckillActivityRepository.findByStatusIn(List.of(0, 1));

            if (activities == null || activities.isEmpty()) {
                return;
            }

            int syncCount = 0;
            for (SeckillActivity activity : activities) {
                if (activity.getSkuId() != null) {
                    try {
                        syncSkuSoldCount(activity.getId(), activity.getSkuId());
                        syncCount++;
                    } catch (Exception e) {
                        log.error("同步SKU已售数量失败: activityId={}, skuId={}, error={}",
                                activity.getId(), activity.getSkuId(), e.getMessage(), e);
                    }
                }
            }

            log.debug("SKU库存同步完成，同步数量: {}", syncCount);

        } catch (Exception e) {
            log.error("SKU库存同步任务异常", e);
        }
    }

    /**
     * 同步单个SKU的已售数量
     */
    private void syncSkuSoldCount(Long activityId, Long skuId) {
        String soldKey = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + skuId;
        String redisSoldStr = stringRedisTemplate.opsForValue().get(soldKey);
        
        if (redisSoldStr == null) {
            return;
        }
        
        try {
            int redisSold = Integer.parseInt(redisSoldStr);
            log.debug("SKU已售数量: activityId={}, skuId={}, sold={}", activityId, skuId, redisSold);
            // SKU级别的同步主要用于监控和统计，数据库层面以活动级别为准
        } catch (NumberFormatException e) {
            log.error("SKU已售数据格式错误: activityId={}, skuId={}, soldStr={}",
                    activityId, skuId, redisSoldStr, e);
        }
    }

    /**
     * 定时校验库存一致性
     * 每5分钟执行一次
     * 检查Redis中的已售数量是否与数据库一致
     */
    @Scheduled(fixedRate = 300000)
    public void validateStockConsistency() {
        log.debug("开始执行库存一致性校验");
        
        try {
            List<SeckillActivity> activities = seckillActivityRepository.findByStatusIn(List.of(0, 1));

            if (activities == null || activities.isEmpty()) {
                return;
            }

            int inconsistentCount = 0;

            for (SeckillActivity activity : activities) {
                boolean consistent = validateActivityStockConsistency(activity);
                if (!consistent) {
                    inconsistentCount++;
                }
            }

            if (inconsistentCount > 0) {
                log.warn("发现 {} 个活动库存数据不一致，建议检查", inconsistentCount);
            } else {
                log.debug("所有活动库存数据一致");
            }

        } catch (Exception e) {
            log.error("库存一致性校验异常", e);
        }
    }

    /**
     * 校验单个活动的库存一致性
     * 比较Redis和数据库中的已售数量是否一致
     *
     * @param activity 秒杀活动
     * @return 是否一致
     */
    private boolean validateActivityStockConsistency(SeckillActivity activity) {
        Long activityId = activity.getId();
        
        // 获取Redis中的已售数量
        String soldKey = SECKILL_SOLD_KEY_PREFIX + activityId;
        String redisSoldStr = stringRedisTemplate.opsForValue().get(soldKey);
        
        if (redisSoldStr == null) {
            // Redis中无数据，如果数据库中已售数量大于0，说明不一致
            if (activity.getSoldCount() > 0) {
                log.warn("活动库存数据不一致: activityId={}, Redis无数据, DB已售={}", 
                        activityId, activity.getSoldCount());
                return false;
            }
            return true;
        }
        
        try {
            int redisSold = Integer.parseInt(redisSoldStr);
            int dbSold = activity.getSoldCount();
            
            // 比较已售数量是否一致
            if (redisSold != dbSold) {
                log.warn("活动已售数量不一致: activityId={}, Redis已售={}, DB已售={}, 差异={}", 
                        activityId, redisSold, dbSold, Math.abs(redisSold - dbSold));
                return false;
            }
            
            // 检查库存计算是否正确
            int availableStock = activity.getStock() - dbSold;
            if (availableStock < 0) {
                log.error("活动库存计算错误: activityId={}, 总库存={}, 已售={}, 可用库存={}",
                        activityId, activity.getStock(), dbSold, availableStock);
                return false;
            }
            
            log.debug("活动库存校验通过: activityId={}, 总库存={}, 已售={}, 可用={}",
                    activityId, activity.getStock(), dbSold, availableStock);
            return true;
            
        } catch (NumberFormatException e) {
            log.error("Redis已售数据格式错误: activityId={}, soldStr={}",
                    activityId, redisSoldStr, e);
            return false;
        }
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
