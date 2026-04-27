package org.example.shoppingserver.util.exception;

import org.example.shoppingserver.common.enums.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于处理业务逻辑中的错误
 *
 * @author MiniMax Agent
 */
@Getter
public class BusinessException extends RuntimeException {

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
     * 业务状态码
     */
    private final String bizCode;

    /**
     * 错误详情
     */
    private final Object details;

    /**
     * 字段错误
     */
    private final java.util.Map<String, String> fieldErrors;

    /**
     * 构造函数(使用ResultCode)
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = mapResultCodeToHttpStatus(resultCode);
        this.bizCode = null;
        this.details = null;
        this.fieldErrors = null;
    }

    /**
     * 构造函数(使用ResultCode和自定义消息)
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.httpStatus = mapResultCodeToHttpStatus(resultCode);
        this.bizCode = null;
        this.details = null;
        this.fieldErrors = null;
    }

    /**
     * 构造函数(使用ResultCode和错误详情)
     */
    public BusinessException(ResultCode resultCode, Object details) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = mapResultCodeToHttpStatus(resultCode);
        this.bizCode = null;
        this.details = details;
        this.fieldErrors = null;
    }

    /**
     * 构造函数(完整参数)
     */
    public BusinessException(ResultCode resultCode, String bizCode, Object details,
                            java.util.Map<String, String> fieldErrors) {

        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = mapResultCodeToHttpStatus(resultCode);
        this.bizCode = bizCode;
        this.details = details;
        this.fieldErrors = fieldErrors;
    }

    /**
     * 构造函数(使用错误码和消息)
     */
    public BusinessException(int code, int httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.bizCode = null;
        this.details = null;
        this.fieldErrors = null;
    }

    /**
     * 构造函数(带业务状态码)
     */
    public BusinessException(int code, int httpStatus, String bizCode, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.bizCode = bizCode;
        this.details = null;
        this.fieldErrors = null;
    }

    /**
     * 构造函数(带字段错误)
     */
    public BusinessException(int code, int httpStatus, java.util.Map<String, String> fieldErrors) {
        super("参数校验失败");
        this.code = code;
        this.httpStatus = httpStatus;
        this.bizCode = null;
        this.details = null;
        this.fieldErrors = fieldErrors;
    }

    /**
     * 构造函数(带Throwable)
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.httpStatus = mapResultCodeToHttpStatus(resultCode);
        this.bizCode = null;
        this.details = null;
        this.fieldErrors = null;
    }

    private int mapResultCodeToHttpStatus(ResultCode resultCode) {
        if (resultCode.isSuccess()) {
            return 200;
        } else if (resultCode.isClientError()) {
            return 400;
        } else if (resultCode.isAuthError()) {
            return 401;
        } else if (resultCode.isBusinessError() || resultCode.isResourceError()) {
            return 400;
        } else if (resultCode.isThirdPartyError() || resultCode.isSystemError()) {
            return 500;
        }
        return 500;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建业务异常
     */
    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    /**
     * 创建带消息的业务异常
     */
    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }

    /**
     * 创建带详情的业务异常
     */
    public static BusinessException of(ResultCode resultCode, Object details) {
        return new BusinessException(resultCode, details);
    }
}
