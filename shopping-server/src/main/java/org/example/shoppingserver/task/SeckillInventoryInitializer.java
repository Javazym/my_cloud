package org.example.shoppingserver.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.model.entity.marketing.SeckillActivity;
import org.example.shoppingserver.model.entity.product.ProductSku;
import org.example.shoppingserver.repository.ProductSkuRepository;
import org.example.shoppingserver.repository.SeckillActivityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀活动库存初始化器
 * 在应用启动时将秒杀活动库存信息加载到Redis中
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillInventoryInitializer implements ApplicationRunner {

    private final SeckillActivityRepository seckillActivityRepository;
    private final ProductSkuRepository productSkuRepository;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis Key前缀：秒杀活动库存
     * 格式：seckill:stock:{activityId}
     */
    private static final String SECKILL_STOCK_KEY_PREFIX = "seckill:stock:";

    /**
     * Redis Key前缀：秒杀活动已售数量
     * 格式：seckill:sold:{activityId}
     */
    private static final String SECKILL_SOLD_KEY_PREFIX = "seckill:sold:";

    /**
     * Redis Key前缀：秒杀活动信息
     * 格式：seckill:info:{activityId}
     */
    private static final String SECKILL_INFO_KEY_PREFIX = "seckill:info:";

    /**
     * Redis Key前缀：秒杀活动SKU库存
     * 格式：seckill:sku:stock:{activityId}:{skuId}
     */
    private static final String SECKILL_SKU_STOCK_KEY_PREFIX = "seckill:sku:stock:";

    /**
     * Redis Key前缀：秒杀活动SKU已售数量
     * 格式：seckill:sku:sold:{activityId}:{skuId}
     */
    private static final String SECKILL_SKU_SOLD_KEY_PREFIX = "seckill:sku:sold:";

    /**
     * 默认过期时间：24小时（秒）
     */
    private static final long DEFAULT_EXPIRE_SECONDS = 24 * 60 * 60;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========== 开始初始化秒杀活动库存到Redis ==========");
        
        try {
            // 查询所有状态为进行中(1)或未开始(0)的秒杀活动
            List<SeckillActivity> activities = seckillActivityRepository.findAll((root, query, cb) -> {
                // 只查询未开始和进行中的活动
                return root.get("status").in(0, 1);
            });

            if (activities == null || activities.isEmpty()) {
                log.info("没有找到需要初始化的秒杀活动");
                return;
            }

            log.info("找到 {} 个秒杀活动，开始初始化到Redis", activities.size());

            int successCount = 0;
            int failCount = 0;

            for (SeckillActivity activity : activities) {
                try {
                    // 检查活动是否有效（时间范围内且有库存）
                    if (!isValidActivity(activity)) {
                        log.debug("跳过无效活动: ID={}, 名称={}", activity.getId(), activity.getName());
                        continue;
                    }

                    // 计算过期时间（活动结束时间与当前时间的差值）
                    long expireSeconds = calculateExpireSeconds(activity.getEndTime());

                    // 存储库存信息
                    String stockKey = SECKILL_STOCK_KEY_PREFIX + activity.getId();
                    stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(activity.getStock()), 
                            expireSeconds, TimeUnit.SECONDS);

                    // 存储已售数量
                    String soldKey = SECKILL_SOLD_KEY_PREFIX + activity.getId();
                    stringRedisTemplate.opsForValue().set(soldKey, String.valueOf(activity.getSoldCount()), 
                            expireSeconds, TimeUnit.SECONDS);

                    // 存储活动基本信息（JSON格式）
                    String infoKey = SECKILL_INFO_KEY_PREFIX + activity.getId();
                    String activityInfo = buildActivityInfo(activity);
                    stringRedisTemplate.opsForValue().set(infoKey, activityInfo, 
                            expireSeconds, TimeUnit.SECONDS);

                    // 如果活动指定了SKU，初始化该SKU的库存
                    if (activity.getSkuId() != null) {
                        initializeSkuStock(activity.getId(), activity.getSkuId(), 
                                activity.getStock(), activity.getSoldCount(), expireSeconds);
                    } else {
                        // 如果未指定SKU，查询商品的所有SKU并均分库存
                        distributeStockToSkus(activity.getId(), activity.getProductId(), 
                                activity.getStock(), activity.getSoldCount(), expireSeconds);
                    }

                    successCount++;
                    log.debug("活动库存初始化成功: ID={}, 名称={}, 库存={}, 已售={}, 过期时间={}秒", 
                            activity.getId(), activity.getName(), activity.getStock(), 
                            activity.getSoldCount(), expireSeconds);

                } catch (Exception e) {
                    failCount++;
                    log.error("活动库存初始化失败: ID={}, 名称={}, 错误: {}", 
                            activity.getId(), activity.getName(), e.getMessage(), e);
                }
            }

            log.info("========== 秒杀活动库存初始化完成 ==========");
            log.info("总计: {}, 成功: {}, 失败: {}", activities.size(), successCount, failCount);

        } catch (Exception e) {
            log.error("秒杀活动库存初始化异常", e);
        }
    }

    /**
     * 检查活动是否有效
     */
    private boolean isValidActivity(SeckillActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        
        // 检查状态：必须是未开始(0)或进行中(1)
        if (activity.getStatus() != 0 && activity.getStatus() != 1) {
            return false;
        }

        // 检查时间：结束时间必须在当前时间之后
        if (activity.getEndTime().isBefore(now)) {
            return false;
        }

        // 检查库存：必须有剩余库存
        if (activity.getStock() <= activity.getSoldCount()) {
            return false;
        }

        return true;
    }

    /**
     * 计算过期时间（秒）
     * 至少保留1小时，最多保留24小时
     */
    private long calculateExpireSeconds(LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(now, endTime).getSeconds();
        
        // 至少1小时，最多24小时
        if (seconds < 3600) {
            return 3600;
        } else if (seconds > DEFAULT_EXPIRE_SECONDS) {
            return DEFAULT_EXPIRE_SECONDS;
        }
        
        return seconds;
    }

    /**
     * 构建活动信息JSON字符串
     */
    private String buildActivityInfo(SeckillActivity activity) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"productId\":%d,\"skuId\":%s,\"seckillPrice\":%s,\"originalPrice\":%s,\"stock\":%d,\"soldCount\":%d,\"limitPerUser\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\",\"status\":%d}",
            activity.getId(),
            escapeJson(activity.getName()),
            activity.getProductId(),
            activity.getSkuId() != null ? activity.getSkuId() : "null",
            activity.getSeckillPrice(),
            activity.getOriginalPrice(),
            activity.getStock(),
            activity.getSoldCount(),
            activity.getLimitPerUser() != null ? activity.getLimitPerUser() : 1,
            activity.getStartTime(),
            activity.getEndTime(),
            activity.getStatus()
        );
    }

    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * 初始化指定SKU的库存
     *
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @param totalStock 总库存
     * @param totalSold 总已售数量
     * @param expireSeconds 过期时间（秒）
     */
    private void initializeSkuStock(Long activityId, Long skuId, Integer totalStock, 
                                     Integer totalSold, long expireSeconds) {
        try {
            // 验证SKU是否存在
            ProductSku sku = productSkuRepository.findById(skuId).orElse(null);
            if (sku == null) {
                log.warn("SKU不存在: activityId={}, skuId={}", activityId, skuId);
                return;
            }

            // 存储SKU库存
            String stockKey = SECKILL_SKU_STOCK_KEY_PREFIX + activityId + ":" + skuId;
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(totalStock), 
                    expireSeconds, TimeUnit.SECONDS);

            // 存储SKU已售数量
            String soldKey = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + skuId;
            stringRedisTemplate.opsForValue().set(soldKey, String.valueOf(totalSold), 
                    expireSeconds, TimeUnit.SECONDS);

            log.debug("SKU库存初始化成功: activityId={}, skuId={}, stock={}, sold={}", 
                    activityId, skuId, totalStock, totalSold);

        } catch (Exception e) {
            log.error("SKU库存初始化失败: activityId={}, skuId={}, error={}", 
                    activityId, skuId, e.getMessage(), e);
        }
    }

    /**
     * 将库存均分到商品的所有SKU
     *
     * @param activityId 活动ID
     * @param productId 商品ID
     * @param totalStock 总库存
     * @param totalSold 总已售数量
     * @param expireSeconds 过期时间（秒）
     */
    private void distributeStockToSkus(Long activityId, Long productId, Integer totalStock,
                                        Integer totalSold, long expireSeconds) {
        try {
            // 查询商品的所有启用SKU
            List<ProductSku> skus = productSkuRepository.findByProductIdAndStatus(productId, 1);
            
            if (skus == null || skus.isEmpty()) {
                log.warn("商品没有可用的SKU: activityId={}, productId={}", activityId, productId);
                return;
            }

            int skuCount = skus.size();
            // 计算每个SKU分配的库存（均分）
            int stockPerSku = totalStock / skuCount;
            int soldPerSku = totalSold / skuCount;
            
            // 处理余数，将剩余库存分配给前几个SKU
            int remainingStock = totalStock % skuCount;
            int remainingSold = totalSold % skuCount;

            log.info("开始分配库存到SKU: activityId={}, 总库存={}, SKU数量={}, 每个SKU基础库存={}", 
                    activityId, totalStock, skuCount, stockPerSku);

            for (int i = 0; i < skus.size(); i++) {
                ProductSku sku = skus.get(i);
                
                // 前remainingStock个SKU多分配1个库存
                int skuStock = stockPerSku + (i < remainingStock ? 1 : 0);
                int skuSold = soldPerSku + (i < remainingSold ? 1 : 0);

                // 存储SKU库存
                String stockKey = SECKILL_SKU_STOCK_KEY_PREFIX + activityId + ":" + sku.getId();
                stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(skuStock), 
                        expireSeconds, TimeUnit.SECONDS);

                // 存储SKU已售数量
                String soldKey = SECKILL_SKU_SOLD_KEY_PREFIX + activityId + ":" + sku.getId();
                stringRedisTemplate.opsForValue().set(soldKey, String.valueOf(skuSold), 
                        expireSeconds, TimeUnit.SECONDS);

                log.debug("SKU库存分配成功: activityId={}, skuId={}, skuCode={}, stock={}, sold={}", 
                        activityId, sku.getId(), sku.getSkuCode(), skuStock, skuSold);
            }

            log.info("SKU库存分配完成: activityId={}, 共分配{}个SKU", activityId, skuCount);

        } catch (Exception e) {
            log.error("SKU库存分配失败: activityId={}, productId={}, error={}", 
                    activityId, productId, e.getMessage(), e);
        }
    }
}
