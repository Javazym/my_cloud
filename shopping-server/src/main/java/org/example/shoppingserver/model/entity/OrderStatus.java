package org.example.shoppingserver.model.entity;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    /**
     * 待付款
     */
    PENDING_PAYMENT(0, "待付款"),
    /**
     * 待发货
     */
    PENDING_SHIPMENT(1, "待发货"),
    /**
     * 待收货
     */
    PENDING_RECEIPT(2, "待收货"),
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    /**
     * 已取消
     */
    CANCELLED(4, "已取消"),
    /**
     * 已退款
     */
    REFUNDED(5, "已退款");

    private final Integer code;
    private final String description;

    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
