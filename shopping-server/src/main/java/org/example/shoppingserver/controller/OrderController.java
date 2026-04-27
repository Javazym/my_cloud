package org.example.shoppingserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.CreateOrderDTO;
import org.example.shoppingserver.model.dto.OrderDTO;
import org.example.shoppingserver.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（用户端）
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单DTO
     * @return 订单信息
     */
    @PostMapping
    public ResponseResult<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        String userId = UserHolder.getCurrentUserId();
        OrderDTO order = orderService.createOrder(userId, createOrderDTO);
        return ResponseResult.success(order);
    }

    /**
     * 获取订单列表
     *
     * @param status   订单状态(可选)
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    @GetMapping
    public ResponseResult<Page<OrderDTO>> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<OrderDTO> orders = orderService.getOrders(userId, status, pageNum, pageSize);
        return ResponseResult.success(orders);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseResult<OrderDTO> getOrderDetail(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderDetailWithLogistics(orderId);
        return ResponseResult.success(order);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseResult<Void> cancelOrder(@PathVariable Long orderId) {
        String userId = UserHolder.getCurrentUserId();
        orderService.cancelOrder(userId, orderId);
        return ResponseResult.success();
    }

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/{orderId}/confirm")
    public ResponseResult<Void> confirmReceipt(@PathVariable Long orderId) {
        String userId = UserHolder.getCurrentUserId();
        orderService.confirmReceipt(userId, orderId);
        return ResponseResult.success();
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @DeleteMapping("/{orderId}")
    public ResponseResult<Void> deleteOrder(@PathVariable Long orderId) {
        String userId = UserHolder.getCurrentUserId();
        orderService.deleteOrder(userId, orderId);
        return ResponseResult.success();
    }

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param payType 支付方式(alipay/wechat)
     * @return 支付结果
     */
    @PostMapping("/{orderId}/pay")
    public ResponseResult<String> payOrder(
            @PathVariable Long orderId,
            @RequestParam String payType) {
        String userId = UserHolder.getCurrentUserId();
        String result = orderService.payOrder(userId, orderId, payType);
        return ResponseResult.success(result);
    }

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @param refundDTO 退款信息
     * @return 操作结果
     */
    @PostMapping("/{orderId}/refund")
    public ResponseResult<Void> applyRefund(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderService.RefundDTO refundDTO) {
        String userId = UserHolder.getCurrentUserId();
        orderService.applyRefund(userId, orderId, refundDTO);
        return ResponseResult.success();
    }
}
