package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.task.SeckillStockSyncTask;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀管理控制器
 * 提供库存同步和校验的管理接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/seckill")
@RequiredArgsConstructor
public class SeckillAdminController {

    private final SeckillStockSyncTask seckillStockSyncTask;

    /**
     * 手动触发库存同步
     * 将Redis中的库存数据立即同步到数据库
     *
     * @return 操作结果
     */
    @PostMapping("/sync")
    public ResponseResult<Void> manualSync() {
        log.info("管理员手动触发库存同步");
        
        try {
            seckillStockSyncTask.manualSync();
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("手动同步失败", e);
            return ResponseResult.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发库存一致性校验
     * 检查Redis和数据库的库存是否一致
     *
     * @return 操作结果
     */
    @PostMapping("/validate")
    public ResponseResult<Void> manualValidate() {
        log.info("管理员手动触发库存校验");
        
        try {
            seckillStockSyncTask.manualValidate();
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("手动校验失败", e);
            return ResponseResult.error("校验失败: " + e.getMessage());
        }
    }

    /**
     * 获取同步任务状态
     *
     * @return 任务状态信息
     */
    @GetMapping("/status")
    public ResponseResult<String> getSyncStatus() {
        String status = "定时任务运行中\n" +
                "- 同步频率: 每30秒\n" +
                "- 校验频率: 每5分钟\n" +
                "- 最后执行: 查看日志";
        
        return ResponseResult.success(status);
    }
}
