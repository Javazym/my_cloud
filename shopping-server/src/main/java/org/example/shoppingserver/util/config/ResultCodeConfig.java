package org.example.shoppingserver.util.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应码配置类
 * 支持从配置文件自定义响应码
 *
 * @author MiniMax Agent
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "response")
public class ResultCodeConfig {

    /**
     * 是否启用自定义响应码
     */
    private boolean enabled = false;

    /**
     * 成功码
     */
    private int successCode = 1000;

    /**
     * 成功消息
     */
    private String successMessage = "操作成功";

    /**
     * 自定义响应码映射
     */
    private Map<Integer, String> codes = new HashMap<>();

    /**
     * 是否包含追踪ID
     */
    private boolean includeTraceId = true;

    /**
     * 是否包含时间戳
     */
    private boolean includeTimestamp = true;

    /**
     * 是否包含请求路径
     */
    private boolean includePath = true;

    /**
     * 是否在生产环境隐藏错误详情
     */
    private boolean hideErrorDetailsInProduction = true;

    /**
     * 生产环境标识
     */
    private String productionProfile = "prod";
}
