package org.example.authserver.util.exception;

import lombok.Getter;
import org.example.authserver.common.enums.ResultCode;


/**
 * 系统异常
 * 用于处理系统级别的错误
 *
 * @author MiniMax Agent
 */
@Getter
public class SystemException extends RuntimeException {

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
     * 错误详情
     */
    private final Object details;

    /**
     * 错误追踪ID
     */
    private final String traceId;

    /**
     * 构造函数
     */
    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = null;
        this.traceId = null;
    }

    /**
     * 构造函数(带消息)
     */
    public SystemException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = null;
        this.traceId = null;
    }

    /**
     * 构造函数(带详情)
     */
    public SystemException(ResultCode resultCode, Object details) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = details;
        this.traceId = null;
    }

    /**
     * 构造函数(完整参数)
     */
    public SystemException(ResultCode resultCode, String message, Object details, String traceId) {
        super(message);
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = details;
        this.traceId = traceId;
    }

    /**
     * 构造函数(带Throwable)
     */
    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = null;
        this.traceId = null;
    }

    /**
     * 构造函数(带完整信息和Throwable)
     */
    public SystemException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
        this.httpStatus = 500;
        this.details = null;
        this.traceId = null;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建系统异常
     */
    public static SystemException of(ResultCode resultCode) {
        return new SystemException(resultCode);
    }

    /**
     * 创建系统异常(带消息)
     */
    public static SystemException of(ResultCode resultCode, String message) {
        return new SystemException(resultCode, message);
    }

    /**
     * 创建系统异常(带详情)
     */
    public static SystemException of(ResultCode resultCode, Object details) {
        return new SystemException(resultCode, details);
    }
}
