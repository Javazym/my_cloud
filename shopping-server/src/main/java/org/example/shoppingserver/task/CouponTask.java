package org.example.shoppingserver.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.service.CouponService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 优惠券定时任务
 * 负责定期清理过期优惠券等维护任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponTask {

    private final CouponService couponService;

    /**
     * 每天凌晨2点执行一次，清理过期优惠券
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredCoupons() {
        log.info("========== 开始执行优惠券清理任务 ==========");
        
        try {
            int count = couponService.cleanExpiredCoupons();
            log.info("优惠券清理任务执行完成，共清理 {} 张过期优惠券", count);
        } catch (Exception e) {
            log.error("优惠券清理任务执行失败", e);
        }
        
        log.info("========== 优惠券清理任务结束 ==========");
    }
}
