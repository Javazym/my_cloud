package org.example.shoppingserver.common.result;

import org.example.shoppingserver.common.enums.HttpStatus;
import org.example.shoppingserver.common.enums.ResultCode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResultBuilder<T> {

    private int code;
    private boolean success;
    private int httpStatus;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String traceId;
    private String path;
    private String method;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPages;
    private Map<String, Object> errors;
    private Map<String, String> fieldErrors;
    private String exceptionType;
    private String stackTraceId;
    private String bizCode;
    private Map<String, Object> extras;
    private Long costTime;

    public ResultBuilder() {
        this.code = ResultCode.SUCCESS.getCode();
        this.success = true;
        this.httpStatus = HttpStatus.OK.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public ResultBuilder<T> ok() {
        this.success = true;
        this.code = ResultCode.SUCCESS.getCode();
        this.httpStatus = HttpStatus.OK.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
        return this;
    }

    public ResultBuilder<T> fail() {
        this.success = false;
        return this;
    }

    public ResultBuilder<T> code(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.success = resultCode.isSuccess();
        this.message = resultCode.getMessage();
        return this;
    }

    public ResultBuilder<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResultBuilder<T> httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus.getCode();
        return this;
    }

    public ResultBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResultBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultBuilder<T> trace(String traceId, String path, String method) {
        this.traceId = traceId;
        this.path = path;
        this.method = method;
        return this;
    }

    public ResultBuilder<T> page(Long total, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (pageSize != null && pageSize > 0 && total != null) {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
        return this;
    }

    public ResultBuilder<T> error(String key, Object value) {
        if (this.errors == null) this.errors = new HashMap<>();
        this.errors.put(key, value);
        return this;
    }

    public ResultBuilder<T> fieldError(String field, String msg) {
        if (this.fieldErrors == null) this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, msg);
        return this;
    }

    public ResultBuilder<T> extra(String key, Object value) {
        if (this.extras == null) this.extras = new HashMap<>();
        this.extras.put(key, value);
        return this;
    }

    public ResultBuilder<T> bizCode(String bizCode) {
        this.bizCode = bizCode;
        return this;
    }

    public ResultBuilder<T> costTime(Long costTime) {
        this.costTime = costTime;
        return this;
    }

    public ResultBuilder<T> exception(String exceptionType, String stackTraceId) {
        this.exceptionType = exceptionType;
        this.stackTraceId = stackTraceId;
        return this;
    }

    public ResponseResult<T> build() {
        return ResponseResult.<T>builder()
                .code(code).success(success).httpStatus(httpStatus).message(message)
                .data(data).timestamp(timestamp).traceId(traceId).path(path).method(method)
                .total(total).pageNum(pageNum).pageSize(pageSize).totalPages(totalPages)
                .errors(errors).fieldErrors(fieldErrors)
                .exceptionType(exceptionType).stackTraceId(stackTraceId)
                .bizCode(bizCode).extras(extras).costTime(costTime)
                .build();
    }

    public static <T> ResultBuilder<T> success() {
        return new ResultBuilder<>();
    }

    public static <T> ResultBuilder<T> success(T data) {
        return new ResultBuilder<T>().data(data);
    }

    public static <T> ResultBuilder<T> error() {
        ResultBuilder<T> b = new ResultBuilder<>();
        b.code(ResultCode.SYSTEM_ERROR).httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return b;
    }

    public static <T> ResultBuilder<T> error(ResultCode code) {
        ResultBuilder<T> b = new ResultBuilder<>();
        b.code(code);
        return b;
    }

    public static <T> ResultBuilder<T> error(int code) {
        ResultBuilder<T> b = new ResultBuilder<>();
        b.code(code).success(false);
        return b;
    }
}