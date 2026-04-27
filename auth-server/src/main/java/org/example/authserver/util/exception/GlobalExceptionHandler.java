package org.example.authserver.util.exception;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolation;
import org.example.authserver.common.ResponseResult;
import org.example.authserver.common.enums.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.cn;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[业务异常] traceId={}, code={}, message={}, path={}",
                traceId, ex.getCode(), ex.getMessage(), request.getRequestURI());

        ResponseResult<Void> result = ResponseResult.<Void>builder()
                .code(ex.getCode())
                .success(false)
                .httpStatus(ex.getHttpStatus())
                .message(ex.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        if (ex.getFieldErrors() != null && !ex.getFieldErrors().isEmpty()) {
            result.setFieldErrors(ex.getFieldErrors());
        }
        if (ex.getDetails() != null) {
            result.addError("details", ex.getDetails());
        }
        if (ex.getBizCode() != null) {
            result.setBizCode(ex.getBizCode());
        }
        return result;
    }

    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleSystemException(SystemException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[系统异常] traceId={}, code={}, message={}, path={}",
                traceId, ex.getCode(), ex.getMessage(), request.getRequestURI(), ex);

        ResponseResult<Void> result = ResponseResult.<Void>builder()
                .code(ex.getCode())
                .success(false)
                .httpStatus(ex.getHttpStatus())
                .message(ex.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();

        if (ex.getDetails() != null) {
            result.addError("details", ex.getDetails());
        }
        result.setStackTraceId(traceId);
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("[参数校验异常] traceId={}, path={}", generateTraceId(), request.getRequestURI());
        String traceId = generateTraceId();
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        e -> e.getDefaultMessage() == null ? "参数错误" : e.getDefaultMessage(),
                        (a, b) -> a
                ));

        log.warn("[参数校验异常] traceId={}, fieldErrors={}, path={}", traceId, fieldErrors, request.getRequestURI());

        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ResultCode.PARAM_ERROR.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleBindException(BindException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, e -> e.getDefaultMessage() == null ? "参数错误" : e.getDefaultMessage(), (a, b) -> a));

        log.warn("[绑定异常] traceId={}, fieldErrors={}, path={}", traceId, fieldErrors, request.getRequestURI());

        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ResultCode.PARAM_ERROR.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        Map<String, String> fieldErrors = new HashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String field = v.getPropertyPath().toString();
            if (field.contains(".")) field = field.substring(field.lastIndexOf(".") + 1);
            fieldErrors.put(field, v.getMessage());
        }
        log.warn("[约束违反异常] traceId={}, fieldErrors={}, path={}", traceId, fieldErrors, request.getRequestURI());

        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ResultCode.PARAM_ERROR.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[缺少请求参数] traceId={}, parameter={}, path={}", traceId, ex.getParameterName(), request.getRequestURI());
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(ex.getParameterName(), "缺少必需参数");

        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_MISSING.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(String.format(ResultCode.PARAM_MISSING.getMessage(), ex.getParameterName()))
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[参数类型不匹配] traceId={}, parameter={}, path={}", traceId, ex.getName(), request.getRequestURI());
        Map<String, String> fieldErrors = new HashMap<>();
        String msg = String.format("参数 '%s' 类型错误", ex.getName());
        fieldErrors.put(ex.getName(), msg);

        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_TYPE_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ResultCode.PARAM_TYPE_ERROR.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[消息不可读] traceId={}, path={}", traceId, request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message("请求体格式错误")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseResult<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[请求方法不支持] traceId={}, method={}, path={}", traceId, ex.getMethod(), request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.METHOD_NOT_SUPPORTED.getCode())
                .success(false)
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("请求方法不支持")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseResult<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[媒体类型不支持] traceId={}, path={}", traceId, request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.MEDIA_TYPE_NOT_SUPPORTED.getCode())
                .success(false)
                .httpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("媒体类型不支持")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseResult<Void> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[404] traceId={}, path={}", traceId, request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.RESOURCE_NOT_FOUND.getCode())
                .success(false)
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .message("资源不存在")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseResult<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[文件过大] traceId={}, path={}", traceId, request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.FILE_SIZE_EXCEEDED.getCode())
                .success(false)
                .httpStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message("文件大小超出限制")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[空指针] traceId={}, path={}", traceId, request.getRequestURI(), ex);
        ResponseResult<Void> result = ResponseResult.<Void>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("系统异常")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
        result.setStackTraceId(traceId);
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[非法参数] traceId={}, msg={}, path={}", traceId, ex.getMessage(), request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.PARAM_INVALID.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[状态异常] traceId={}, msg={}, path={}", traceId, ex.getMessage(), request.getRequestURI());
        return ResponseResult.<Void>builder()
                .code(ResultCode.STATUS_ILLEGAL.getCode())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleException(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[未知异常] traceId={}, path={}", traceId, request.getRequestURI(), ex);
        ResponseResult<Void> result = ResponseResult.<Void>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .success(false)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("系统异常")
                .traceId(traceId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
        result.setStackTraceId(traceId);
        result.setExceptionType(ex.getClass().getName());
        return result;
    }

    private String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }
}