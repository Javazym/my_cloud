package org.example.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一 API 响应结果
 * 用于微服务间 Feign 调用的标准化响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应状态码
     */
    private int code;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResult<T> success() {
        return ApiResult.<T>builder()
                .code(200)
                .success(true)
                .message("操作成功")
                .build();
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .code(200)
                .success(true)
                .message("操作成功")
                .data(data)
                .build();
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResult<T> error(String message) {
        return ApiResult.<T>builder()
                .code(500)
                .success(false)
                .message(message)
                .build();
    }
    
    /**
     * 失败响应（带状态码）
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return ApiResult.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .build();
    }
}
