package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.task.SeckillInventoryInitializer;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀管理控制器
 * 提供秒杀活动的手动管理功能
 */
@Slf4j
@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillInventoryInitializer seckillInventoryInitializer;

    /**
     * 手动刷新秒杀活动库存到Redis
     * 用于在创建或修改秒杀活动后，立即同步到Redis
     *
     * @return 操作结果
     */
    @PostMapping("/refresh-stock")
    public ResponseResult<Void> refreshSeckillStock() {
        log.info("收到手动刷新秒杀库存请求");
        
        try {
            // 重新执行初始化逻辑
            seckillInventoryInitializer.run(null);
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("刷新秒杀库存失败", e);
            return ResponseResult.error("刷新失败: " + e.getMessage());
        }
    }

    /**
     * 获取Redis中指定活动的库存信息
     *
     * @param activityId 活动ID
     * @return 库存信息
     */
    @GetMapping("/stock/{activityId}")
    public ResponseResult<String> getSeckillStock(@PathVariable Long activityId) {
        String stockKey = "seckill:stock:" + activityId;
        String soldKey = "seckill:sold:" + activityId;
        
        // 这里需要通过RedisTemplate获取，但由于是演示，我们返回提示信息
        return ResponseResult.success("请使用Redis客户端查询 key: " + stockKey + " 和 " + soldKey);
    }
}
