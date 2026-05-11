package org.example.shoppingserver.model.entity.common;

/**
 * 审核状态枚举
 */
public enum AuditStatus {
    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    /**
     * 通过
     */
    APPROVED(1, "通过"),
    /**
     * 驳回
     */
    REJECTED(2, "驳回");

    private final Integer code;
    private final String description;

    AuditStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AuditStatus fromCode(Integer code) {
        for (AuditStatus status : AuditStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
