package org.example.shoppingserver.model.entity;

/**
 * 退款状态枚举
 */
public enum RefundStatus {
    /**
     * 申请中
     */
    APPLYING(0, "申请中"),
    /**
     * 同意
     */
    AGREED(1, "同意"),
    /**
     * 拒绝
     */
    REJECTED(2, "拒绝"),
    /**
     * 已退款
     */
    REFUNDED(3, "已退款");

    private final Integer code;
    private final String description;

    RefundStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RefundStatus fromCode(Integer code) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
