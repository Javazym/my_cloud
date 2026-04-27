package org.example.mygateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 动态路由监听器
 * 作用：监听 Nacos 配置变化，自动更新网关路由
 */
@Component
@DependsOn("nacosConfigManager") // 确保 Nacos 先启动
public class DynamicRouteService {

    private static final Logger log = LoggerFactory.getLogger(DynamicRouteService.class);

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private NacosConfigManager nacosConfigManager;

    /**
     * 项目启动后自动执行
     */

    public void init() {
        log.info("=== 启动动态路由监听器 ===");

        try {
            String dataId = "gateway-routes.yaml";
            String group = "DEFAULT_GROUP";

            // 添加监听器
            nacosConfigManager.getConfigService().addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("🔥 检测到 Nacos 路由配置变更！正在刷新...");
                    // 这里我们利用 Spring 的机制，直接发布刷新事件
                    // 配合 @ConfigurationProperties 绑定，Spring 会自动处理大部分工作
                    // 但为了保险，我们强制清空并重新加载（简单粗暴但有效）
                    refreshRoutes();
                }

                @Override
                public Executor getExecutor() {
                    return null; // 使用默认线程
                }
            });
            log.info("✅ 动态路由监听器启动成功，监听 DataID: {}", dataId);

            // 启动时也加载一次
            refreshRoutes();

        } catch (Exception e) {
            log.error("❌ 启动动态路由监听器失败", e);
        }
    }

    /**
     * 刷新路由方法
     * 逻辑：发布一个 RefreshRoutesEvent 事件，告诉网关“配置变了，请重新读取”
     */
    private void refreshRoutes() {
        // 发送刷新事件
        publisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("🚀 路由刷新事件已发送，网关正在重新加载路由规则...");
    }
}
