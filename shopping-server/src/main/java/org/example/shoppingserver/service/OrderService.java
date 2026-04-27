package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.CreateOrderDTO;
import org.example.shoppingserver.model.dto.OrderDTO;
import org.example.shoppingserver.model.dto.OrderLogisticsDTO;
import org.springframework.data.domain.Page;

/**
 * 订单Service接口
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId        用户ID
     * @param createOrderDTO 创建订单DTO
     * @return 订单信息
     */
    OrderDTO createOrder(String userId, CreateOrderDTO createOrderDTO);

    /**
     * 获取订单列表（用户端）
     *
     * @param userId   用户ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    Page<OrderDTO> getOrders(String userId, Integer status, int pageNum, int pageSize);

    /**
     * 获取订单列表（商家端）
     *
     * @param merchantId 商家ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    Page<OrderDTO> getMerchantOrders(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetail(Long orderId);

    /**
     * 获取订单详情（包含完整物流信息）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetailWithLogistics(Long orderId);

    /**
     * 取消订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(String userId, Long orderId);

    /**
     * 确认收货
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean confirmReceipt(String userId, Long orderId);

    /**
     * 删除订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean deleteOrder(String userId, Long orderId);

    /**
     * 支付订单
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param payType 支付方式
     * @return 支付结果
     */
    String payOrder(String userId, Long orderId, String payType);

    /**
     * 模拟支付回调
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean payCallback(Long orderId);

    /**
     * 发货（商家端）
     *
     * @param merchantId 商家ID
     * @param orderId 订单ID
     * @param logisticsDTO 物流信息
     */
    void shipOrder(Long merchantId, Long orderId, OrderLogisticsDTO logisticsDTO);

    /**
     * 申请退款
     *
     * @param userId     用户ID
     * @param orderId    订单ID
     * @param refundDTO  退款DTO
     * @return 是否成功
     */
    boolean applyRefund(String userId, Long orderId, RefundDTO refundDTO);

    /**
     * 同意退款申请（商家端）
     *
     * @param merchantId 商家ID
     * @param refundId 退款ID
     */
    void approveRefund(Long merchantId, Long refundId);

    /**
     * 拒绝退款申请（商家端）
     *
     * @param merchantId 商家ID
     * @param refundId 退款ID
     * @param reason 拒绝原因
     */
    void rejectRefund(Long merchantId, Long refundId, String reason);

    /**
     * 获取商家退款申请列表
     *
     * @param merchantId 商家ID
     * @param status 退款状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    Page<OrderDTO.RefundVO> getMerchantRefunds(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取订单状态
     *
     * @param orderId 订单ID
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 获取商家订单统计信息
     *
     * @param merchantId 商家ID
     * @return 订单统计
     */
    OrderStatistics getMerchantOrderStatistics(Long merchantId);

    /**
     * 退款DTO
     */
    class RefundDTO {
        private Integer type;
        private java.math.BigDecimal amount;
        private String reason;
        private String description;

        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 订单统计信息
     */
    class OrderStatistics {
        private Long totalOrders;           // 总订单数
        private Long pendingPayment;        // 待付款
        private Long pendingShipment;       // 待发货
        private Long pendingReceipt;        // 待收货
        private Long completed;             // 已完成
        private Long cancelled;             // 已取消
        private java.math.BigDecimal todaySales;      // 今日销售额
        private java.math.BigDecimal monthSales;      // 本月销售额

        public OrderStatistics() {}

        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public Long getPendingPayment() { return pendingPayment; }
        public void setPendingPayment(Long pendingPayment) { this.pendingPayment = pendingPayment; }
        public Long getPendingShipment() { return pendingShipment; }
        public void setPendingShipment(Long pendingShipment) { this.pendingShipment = pendingShipment; }
        public Long getPendingReceipt() { return pendingReceipt; }
        public void setPendingReceipt(Long pendingReceipt) { this.pendingReceipt = pendingReceipt; }
        public Long getCompleted() { return completed; }
        public void setCompleted(Long completed) { this.completed = completed; }
        public Long getCancelled() { return cancelled; }
        public void setCancelled(Long cancelled) { this.cancelled = cancelled; }
        public java.math.BigDecimal getTodaySales() { return todaySales; }
        public void setTodaySales(java.math.BigDecimal todaySales) { this.todaySales = todaySales; }
        public java.math.BigDecimal getMonthSales() { return monthSales; }
        public void setMonthSales(java.math.BigDecimal monthSales) { this.monthSales = monthSales; }
    }
}