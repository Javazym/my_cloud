package org.example.commonapi.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 定义了系统常用的响应状态码
 *
 * @author MiniMax Agent
 */
@Getter
public enum ResultCode {

    // ========== 成功状态码 (1xxx) ==========
    /** 操作成功 */
    SUCCESS(1000, "操作成功"),

    /** 查询成功 */
    QUERY_SUCCESS(1001, "查询成功"),

    /** 创建成功 */
    CREATE_SUCCESS(1002, "创建成功"),

    /** 更新成功 */
    UPDATE_SUCCESS(1003, "更新成功"),

    /** 删除成功 */
    DELETE_SUCCESS(1004, "删除成功"),

    /** 上传成功 */
    UPLOAD_SUCCESS(1005, "上传成功"),

    /** 下载成功 */
    DOWNLOAD_SUCCESS(1006, "下载成功"),

    /** 导出成功 */
    EXPORT_SUCCESS(1007, "导出成功"),

    /** 导入成功 */
    IMPORT_SUCCESS(1008, "导入成功"),

    /** 登录成功 */
    LOGIN_SUCCESS(1009, "登录成功"),

    /** 登出成功 */
    LOGOUT_SUCCESS(1010, "退出登录成功"),

    /** 发送成功 */
    SEND_SUCCESS(1011, "发送成功"),

    /** 验证成功 */
    VERIFY_SUCCESS(1012, "验证成功"),

    // ========== 客户端错误状态码 (2xxx) ==========
    /** 请求参数错误 */
    PARAM_ERROR(2000, "请求参数错误"),

    /** 缺少必需参数 */
    PARAM_MISSING(2001, "缺少必需参数: %s"),

    /** 参数格式错误 */
    PARAM_FORMAT_ERROR(2002, "参数格式错误: %s"),

    /** 参数范围错误 */
    PARAM_RANGE_ERROR(2003, "参数范围错误: %s"),

    /** 参数类型错误 */
    PARAM_TYPE_ERROR(2004, "参数类型错误: %s"),

    /** 参数值无效 */
    PARAM_INVALID(2005, "参数值无效: %s"),

    /** 请求方法不支持 */
    METHOD_NOT_SUPPORTED(2006, "请求方法不支持"),

    /** 请求媒体类型不支持 */
    MEDIA_TYPE_NOT_SUPPORTED(2007, "请求媒体类型不支持"),

    /** 请求体过大 */
    REQUEST_BODY_TOO_LARGE(2008, "请求体过大"),

    /** 请求URL过长 */
    REQUEST_URL_TOO_LONG(2009, "请求URL过长"),

    /** 请求次数过多 */
    REQUEST_TOO_FREQUENT(2010, "请求过于频繁，请稍后再试"),

    /** 请求已过期 */
    REQUEST_EXPIRED(2011, "请求已过期"),

    /** 重复请求 */
    DUPLICATE_REQUEST(2012, "请勿重复提交请求"),

    // ========== 认证授权错误状态码 (3xxx) ==========
    /** 用户未登录 */
    NOT_LOGIN(3000, "用户未登录，请先登录"),

    /** 登录已过期 */
    LOGIN_EXPIRED(3001, "登录已过期，请重新登录"),

    /** 账号已被禁用 */
    ACCOUNT_DISABLED(3002, "账号已被禁用"),

    /** 账号已被锁定 */
    ACCOUNT_LOCKED(3003, "账号已被锁定，请稍后再试"),

    /** 密码错误 */
    PASSWORD_ERROR(3004, "密码错误，您还有 %d 次尝试机会"),

    /** 密码已过期 */
    PASSWORD_EXPIRED(3005, "密码已过期，请修改密码"),

    /** 验证码错误 */
    VERIFY_CODE_ERROR(3006, "验证码错误"),

    /** 验证码已过期 */
    VERIFY_CODE_EXPIRED(3007, "验证码已过期"),

    /** Token无效 */
    TOKEN_INVALID(3008, "Token无效"),

    /** Token已过期 */
    TOKEN_EXPIRED(3009, "Token已过期"),

    /** 无访问权限 */
    ACCESS_DENIED(3010, "您没有访问该资源的权限"),

    /** 权限不足 */
    PERMISSION_DENIED(3011, "权限不足，无法执行此操作"),

    /** 角色不匹配 */
    ROLE_MISMATCH(3012, "您的角色无权执行此操作"),

    /** 用户不存在 */
    USER_NOT_FOUND(3013, "用户不存在"),

    /** 账号已被注册 */
    ACCOUNT_ALREADY_EXISTS(3014, "该账号已被注册"),

    /** 手机号已被绑定 */
    PHONE_ALREADY_BOUND(3015, "该手机号已被其他账号绑定"),

    /** 邮箱已被绑定 */
    EMAIL_ALREADY_BOUND(3016, "该邮箱已被其他账号绑定"),

    // ========== 业务逻辑错误状态码 (4xxx) ==========
    /** 业务处理失败 */
    BUSINESS_ERROR(4000, "业务处理失败: %s"),

    /** 数据不存在 */
    DATA_NOT_FOUND(4001, "数据不存在"),

    /** 数据已存在 */
    DATA_ALREADY_EXISTS(4002, "数据已存在"),

    /** 数据已过期 */
    DATA_EXPIRED(4003, "数据已过期"),

    /** 数据被占用 */
    DATA_IN_USE(4004, "该数据正在被使用，无法操作"),

    /** 操作被拒绝 */
    OPERATION_REJECTED(4005, "操作被拒绝: %s"),

    /** 状态不合法 */
    STATUS_ILLEGAL(4006, "当前状态不允许此操作"),

    /** 余额不足 */
    BALANCE_INSUFFICIENT(4007, "余额不足"),

    /** 库存不足 */
    STOCK_INSUFFICIENT(4008, "库存不足"),

    /** 配额已用完 */
    QUOTA_EXHAUSTED(4009, "配额已用完，请升级或等待重置"),

    /** 次数已用完 */
    COUNT_EXHAUSTED(4010, "可用次数已用完"),

    /** 时间窗口限制 */
    TIME_WINDOW_LIMIT(4011, "请在规定时间内完成操作"),

    /** 需要实名认证 */
    REAL_NAME_REQUIRED(4012, "请先完成实名认证"),

    /** 需要设置支付密码 */
    PAY_PASSWORD_REQUIRED(4013, "请先设置支付密码"),

    /** 支付密码错误 */
    PAY_PASSWORD_ERROR(4014, "支付密码错误"),

    // ========== 资源错误状态码 (5xxx) ==========
    /** 资源不存在 */
    RESOURCE_NOT_FOUND(5000, "请求的资源不存在"),

    /** 资源已存在 */
    RESOURCE_ALREADY_EXISTS(5001, "资源已存在"),

    /** 资源被占用 */
    RESOURCE_IN_USE(5002, "资源正在被使用"),

    /** 资源创建失败 */
    RESOURCE_CREATE_FAILED(5003, "资源创建失败"),

    /** 资源更新失败 */
    RESOURCE_UPDATE_FAILED(5004, "资源更新失败"),

    /** 资源删除失败 */
    RESOURCE_DELETE_FAILED(5005, "资源删除失败"),

    /** 文件不存在 */
    FILE_NOT_FOUND(5006, "文件不存在"),

    /** 文件上传失败 */
    FILE_UPLOAD_FAILED(5007, "文件上传失败"),

    /** 文件下载失败 */
    FILE_DOWNLOAD_FAILED(5008, "文件下载失败"),

    /** 文件格式不支持 */
    FILE_FORMAT_NOT_SUPPORTED(5009, "文件格式不支持"),

    /** 文件大小超限 */
    FILE_SIZE_EXCEEDED(5010, "文件大小超出限制"),

    /** 图片上传失败 */
    IMAGE_UPLOAD_FAILED(5011, "图片上传失败"),

    /** 视频上传失败 */
    VIDEO_UPLOAD_FAILED(5012, "视频上传失败"),

    // ========== 第三方服务错误状态码 (6xxx) ==========
    /** 第三方服务不可用 */
    THIRD_PARTY_UNAVAILABLE(6000, "第三方服务暂时不可用"),

    /** 第三方服务超时 */
    THIRD_PARTY_TIMEOUT(6001, "第三方服务响应超时"),

    /** 第三方服务错误 */
    THIRD_PARTY_ERROR(6002, "第三方服务返回错误: %s"),

    /** 短信发送失败 */
    SMS_SEND_FAILED(6003, "短信发送失败"),

    /** 邮件发送失败 */
    EMAIL_SEND_FAILED(6004, "邮件发送失败"),

    /** 支付失败 */
    PAYMENT_FAILED(6005, "支付失败: %s"),

    /** 退款失败 */
    REFUND_FAILED(6006, "退款失败: %s"),

    // ========== 系统错误状态码 (9xxx) ==========
    /** 系统内部错误 */
    SYSTEM_ERROR(9000, "系统内部错误，请稍后再试"),

    /** 系统繁忙 */
    SYSTEM_BUSY(9001, "系统繁忙，请稍后再试"),

    /** 系统维护中 */
    SYSTEM_MAINTENANCE(9002, "系统正在维护中，请稍后再试"),

    /** 数据库错误 */
    DATABASE_ERROR(9003, "数据库操作失败"),

    /** 缓存服务错误 */
    CACHE_ERROR(9004, "缓存服务异常"),

    /** 消息队列错误 */
    MQ_ERROR(9005, "消息队列异常"),

    /** 文件服务错误 */
    FILE_SERVICE_ERROR(9006, "文件服务异常"),

    /** 网络连接错误 */
    NETWORK_ERROR(9007, "网络连接异常"),

    /** 服务调用超时 */
    SERVICE_TIMEOUT(9008, "服务调用超时"),

    /** 服务不可用 */
    SERVICE_UNAVAILABLE(9009, "服务暂时不可用"),

    /** 熔断触发 */
    CIRCUIT_BREAKER_TRIGGERED(9010, "请求过于频繁，触发了流量限制"),

    /** 限流触发 */
    RATE_LIMIT_TRIGGERED(9011, "请求过于频繁，请稍后再试"),

    // ========== 自定义业务错误码区间 ==========
    // 预留: 10000-19999 (业务自定义)
    // 预留: 20000-29999 (模块自定义)
    // 预留: 30000-39999 (租户自定义)

    /** 未知错误 */
    UNKNOWN_ERROR(9999, "未知错误");

    /** 状态码 */
    private final int code;

    /** 消息模板 */
    private final String message;

    // ✅ 关键修复：手动定义构造器（不能用 @AllArgsConstructor）
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.code == code) {
                return resultCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 获取格式化后的消息
     */
    public String getMessage(Object... args) {
        if (args == null || args.length == 0) {
            return this.message;
        }
        return String.format(this.message, args);
    }

    public boolean isSuccess() {
        return code >= 1000 && code < 2000;
    }

    public boolean isClientError() {
        return code >= 2000 && code < 3000;
    }

    public boolean isAuthError() {
        return code >= 3000 && code < 4000;
    }

    public boolean isBusinessError() {
        return code >= 4000 && code < 5000;
    }

    public boolean isResourceError() {
        return code >= 5000 && code < 6000;
    }

    public boolean isThirdPartyError() {
        return code >= 6000 && code < 7000;
    }

    public boolean isSystemError() {
        return code >= 9000;
    }
}