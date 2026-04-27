package org.example.shoppingserver.common.result;


import org.example.shoppingserver.common.enums.HttpStatus;
import org.example.shoppingserver.common.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果封装类
 * 提供标准化的API响应结构
 *
 * @param <T> 响应数据类型
 * @author MiniMax Agent
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 核心字段 ====================

    /**
     * 响应状态码
     * 对应ResultCode枚举
     */
    private int code;

    /**
     * 响应状态
     * true: 成功, false: 失败
     */
    private boolean success;

    /**
     * HTTP状态码
     * 用于HTTP响应状态
     */
    private int httpStatus;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // ==================== 扩展字段 ====================

    /**
     * 响应时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 请求追踪ID
     * 用于日志追踪和问题排查
     */
    private String traceId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    // ==================== 分页相关字段 ====================

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;

    // ==================== 错误详情字段 ====================

    /**
     * 错误详情
     * 包含更详细的错误信息，用于调试
     */
    private Map<String, Object> errors;

    /**
     * 错误字段
     * 用于参数校验错误，key为字段名，value为错误信息
     */
    private Map<String, String> fieldErrors;

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 堆栈跟踪ID
     * 当发生异常时，生成唯一ID用于关联日志
     */
    private String stackTraceId;

    // ==================== 业务相关字段 ====================

    /**
     * 业务状态码
     * 用于更细粒度的业务状态标识
     */
    private String bizCode;

    /**
     * 附加数据
     * 用于传递额外的业务数据
     */
    private Map<String, Object> extras;

    /**
     * 响应耗时(毫秒)
     */
    private Long costTime;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功响应(无数据)
     */
    public static <T> ResponseResult<T> success() {
        return success(ResultCode.SUCCESS);
    }

    /**
     * 创建成功响应(带数据)
     */
    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .success(true)
                .httpStatus(HttpStatus.OK.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功响应(带消息)
     */
    public static <T> ResponseResult<T> success(String message, T data) {
        return ResponseResult.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .success(true)
                .httpStatus(HttpStatus.OK.getCode())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功响应(带状态码和消息)
     */
    public static <T> ResponseResult<T> success(ResultCode resultCode) {
        return ResponseResult.<T>builder()
                .code(resultCode.getCode())
                .success(true)
                .httpStatus(HttpStatus.OK.getCode())
                .message(resultCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功响应(带状态码、消息和数据)
     */
    public static <T> ResponseResult<T> success(ResultCode resultCode, T data) {
        return ResponseResult.<T>builder()
                .code(resultCode.getCode())
                .success(true)
                .httpStatus(HttpStatus.OK.getCode())
                .message(resultCode.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应(无数据)
     */
    public static <T> ResponseResult<T> error() {
        return error(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 创建失败响应(带ResultCode)
     */
    public static <T> ResponseResult<T> error(ResultCode resultCode) {
        HttpStatus httpStatus = mapResultCodeToHttpStatus(resultCode);
        return ResponseResult.<T>builder()
                .code(resultCode.getCode())
                .success(false)
                .httpStatus(httpStatus.getCode())
                .message(resultCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应(带消息)
     */
    public static <T> ResponseResult<T> error(String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应(带状态码和消息)
     */
    public static <T> ResponseResult<T> error(int code, String message) {
        HttpStatus httpStatus = mapCodeToHttpStatus(code);
        return ResponseResult.<T>builder()
                .code(code)
                .success(false)
                .httpStatus(httpStatus.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应(带HTTP状态码)
     */
    public static <T> ResponseResult<T> error(HttpStatus httpStatus) {
        return ResponseResult.<T>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .success(false)
                .httpStatus(httpStatus.getCode())
                .message(httpStatus.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败响应(带HTTP状态码和消息)
     */
    public static <T> ResponseResult<T> error(HttpStatus httpStatus, String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .success(false)
                .httpStatus(httpStatus.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建参数错误响应
     */
    public static <T> ResponseResult<T> paramError(String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.PARAM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建未授权响应
     */
    public static <T> ResponseResult<T> unauthorized() {
        return unauthorized(ResultCode.NOT_LOGIN.getMessage());
    }

    /**
     * 创建未授权响应(带消息)
     */
    public static <T> ResponseResult<T> unauthorized(String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.NOT_LOGIN.getCode())
                .success(false)
                .httpStatus(HttpStatus.UNAUTHORIZED.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建禁止访问响应
     */
    public static <T> ResponseResult<T> forbidden() {
        return forbidden(ResultCode.ACCESS_DENIED.getMessage());
    }

    /**
     * 创建禁止访问响应(带消息)
     */
    public static <T> ResponseResult<T> forbidden(String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.ACCESS_DENIED.getCode())
                .success(false)
                .httpStatus(HttpStatus.FORBIDDEN.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建未找到响应
     */
    public static <T> ResponseResult<T> notFound() {
        return notFound(ResultCode.RESOURCE_NOT_FOUND.getMessage());
    }

    /**
     * 创建未找到响应(带消息)
     */
    public static <T> ResponseResult<T> notFound(String message) {
        return ResponseResult.<T>builder()
                .code(ResultCode.RESOURCE_NOT_FOUND.getCode())
                .success(false)
                .httpStatus(HttpStatus.NOT_FOUND.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ==================== 业务方法 ====================

    /**
     * 判断是否为成功响应
     */
    public boolean isOk() {
        return this.success;
    }

    /**
     * 判断是否为失败响应
     */
    public boolean isFail() {
        return !this.success;
    }

    /**
     * 设置错误详情
     */
    public ResponseResult<T> errors(Map<String, Object> errors) {
        this.errors = errors;
        return this;
    }

    /**
     * 添加错误详情
     */
    public ResponseResult<T> addError(String key, Object value) {
        if (this.errors == null) {
            this.errors = new HashMap<>();
        }
        this.errors.put(key, value);
        return this;
    }

    /**
     * 设置字段错误
     */
    public ResponseResult<T> fieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
        return this;
    }

    /**
     * 添加字段错误
     */
    public ResponseResult<T> addFieldError(String field, String message) {
        if (this.fieldErrors == null) {
            this.fieldErrors = new HashMap<>();
        }
        this.fieldErrors.put(field, message);
        return this;
    }

    /**
     * 设置附加数据
     */
    public ResponseResult<T> extras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    /**
     * 添加附加数据
     */
    public ResponseResult<T> addExtra(String key, Object value) {
        if (this.extras == null) {
            this.extras = new HashMap<>();
        }
        this.extras.put(key, value);
        return this;
    }

    /**
     * 获取附加数据
     */
    @SuppressWarnings("unchecked")
    public <E> E getExtra(String key) {
        if (this.extras == null) {
            return null;
        }
        return (E) this.extras.get(key);
    }

    /**
     * 设置请求追踪信息
     */
    public ResponseResult<T> trace(String traceId, String path, String method) {
        this.traceId = traceId;
        this.path = path;
        this.method = method;
        return this;
    }

    /**
     * 设置分页信息
     */
    public ResponseResult<T> page(Long total, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (pageSize != null && pageSize > 0 && total != null) {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
        return this;
    }


    /**
     * 转换为Map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", this.code);
        map.put("success", this.success);
        map.put("httpStatus", this.httpStatus);
        map.put("message", this.message);
        map.put("timestamp", this.timestamp);
        if (this.data != null) {
            map.put("data", this.data);
        }
        if (this.traceId != null) {
            map.put("traceId", this.traceId);
        }
        if (this.total != null) {
            map.put("total", this.total);
        }
        if (this.pageNum != null) {
            map.put("pageNum", this.pageNum);
        }
        if (this.pageSize != null) {
            map.put("pageSize", this.pageSize);
        }
        if (this.totalPages != null) {
            map.put("totalPages", this.totalPages);
        }
        if (this.errors != null) {
            map.put("errors", this.errors);
        }
        if (this.fieldErrors != null) {
            map.put("fieldErrors", this.fieldErrors);
        }
        if (this.extras != null) {
            map.put("extras", this.extras);
        }
        if (this.costTime != null) {
            map.put("costTime", this.costTime);
        }
        return map;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ResultCode映射到HttpStatus
     */
    private static HttpStatus mapResultCodeToHttpStatus(ResultCode resultCode) {
        if (resultCode.isSuccess()) {
            return HttpStatus.OK;
        } else if (resultCode.isClientError()) {
            return HttpStatus.BAD_REQUEST;
        } else if (resultCode.isAuthError()) {
            return HttpStatus.UNAUTHORIZED;
        } else if (resultCode.isBusinessError() || resultCode.isResourceError()) {
            return HttpStatus.BAD_REQUEST;
        } else if (resultCode.isThirdPartyError() || resultCode.isSystemError()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 根据状态码范围映射到HttpStatus
     */
    private static HttpStatus mapCodeToHttpStatus(int code) {
        if (code >= 1000 && code < 2000) {
            return HttpStatus.OK;
        } else if (code >= 2000 && code < 3000) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 3000 && code < 4000) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code >= 4000 && code < 5000) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 5000 && code < 6000) {
            return HttpStatus.NOT_FOUND;
        } else if (code >= 9000) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.OK;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", success=" + success +
                ", httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", traceId='" + traceId + '\'' +
                ", total=" + total +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
