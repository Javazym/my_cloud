package org.example.authserver.util.exception;

import lombok.Getter;
import org.example.authserver.common.enums.ResultCode;


import java.util.HashMap;
import java.util.Map;

/**
 * 参数校验异常
 * 用于处理参数校验失败的情况
 *
 * @author MiniMax Agent
 */
@Getter
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * HTTP状态码
     */
    private final int httpStatus;

    /**
     * 字段错误
     */
    private final Map<String, String> fieldErrors;

    /**
     * 错误详情
     */
    private final Object details;

    /**
     * 构造函数(使用ResultCode)
     */
    public ValidationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = 400;
        this.fieldErrors = new HashMap<>();
        this.details = null;
    }

    /**
     * 构造函数(带自定义消息)
     */
    public ValidationException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.httpStatus = 400;
        this.fieldErrors = new HashMap<>();
        this.details = null;
    }

    /**
     * 构造函数(带字段错误)
     */
    public ValidationException(ResultCode resultCode, Map<String, String> fieldErrors) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = 400;
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        this.details = null;
    }

    /**
     * 构造函数(完整参数)
     */
    public ValidationException(ResultCode resultCode, String message,
                               Map<String, String> fieldErrors, Object details) {
        super(message);
        this.code = resultCode.getCode();
        this.httpStatus = 400;
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        this.details = details;
    }

    /**
     * 构造函数(单字段错误)
     */
    public ValidationException(String field, String message) {
        super("参数校验失败");
        this.code = ResultCode.PARAM_ERROR.getCode();
        this.httpStatus = 400;
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, message);
        this.details = null;
    }

    /**
     * 添加字段错误
     */
    public ValidationException addFieldError(String field, String message) {
        this.fieldErrors.put(field, message);
        return this;
    }

    /**
     * 添加多个字段错误
     */
    public ValidationException addFieldErrors(Map<String, String> errors) {
        if (errors != null) {
            this.fieldErrors.putAll(errors);
        }
        return this;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建参数校验异常
     */
    public static ValidationException of(ResultCode resultCode) {
        return new ValidationException(resultCode);
    }

    /**
     * 创建参数校验异常(带消息)
     */
    public static ValidationException of(ResultCode resultCode, String message) {
        return new ValidationException(resultCode, message);
    }

    /**
     * 创建参数校验异常(带字段错误)
     */
    public static ValidationException of(ResultCode resultCode, Map<String, String> fieldErrors) {
        return new ValidationException(resultCode, fieldErrors);
    }

    /**
     * 创建单字段错误异常
     */
    public static ValidationException of(String field, String message) {
        return new ValidationException(field, message);
    }
}
