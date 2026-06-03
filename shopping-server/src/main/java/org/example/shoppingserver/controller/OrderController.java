package org.example.shoppingserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.order.CreateOrderDTO;
import org.example.shoppingserver.model.dto.order.RefundDTO;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.order.RefundVO;
import org.example.shoppingserver.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（用户端）
 */
@Slf4j
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
    public ResponseResult<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        OrderVO order = orderService.checkOrder(createOrderDTO);
        return ResponseResult.success(order);
    }

    /**
     * 秒杀活动订单创建（高性能异步版）
     *
     * @param dto 创建秒杀活动订单DTO
     * @return 秒杀活动订单信息
     */
    @PostMapping("/seckill/{activityId}")
    public ResponseResult<OrderVO> createSeckillActivityOrder(@Valid @RequestBody CreateOrderDTO dto, @PathVariable Long activityId) {
        String userId = UserHolder.getCurrentUserId();
        Long skuId = dto.getItems().get(0).getSkuId();
        
        try {
            OrderVO order = orderService.createSeckillOrder(userId, activityId, skuId, dto);
            return ResponseResult.success("秒杀成功，订单创建中", order);
        } catch (RuntimeException e) {
            log.error("秒杀失败: userId={}, activityId={}, error={}", 
                    userId, activityId, e.getMessage());
            return ResponseResult.error(e.getMessage());
        }
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
    public ResponseResult<Page<OrderVO>> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<OrderVO> orders = orderService.getOrders(userId, status, pageNum, pageSize);
        return ResponseResult.success(orders);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseResult<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO order = orderService.getOrderDetailWithLogistics(orderId);
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
     * @return 支付结果
     */
    @PostMapping("/{orderId}/pay")
    public ResponseResult<String> payOrder(
            @PathVariable Long orderId) {
        String userId = UserHolder.getCurrentUserId();
        String result = orderService.payOrder(userId, orderId);
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
    public ResponseResult<?> applyRefund(
            @PathVariable Long orderId,
            @Valid @RequestBody RefundDTO refundDTO) {
        String userId = UserHolder.getCurrentUserId();
        orderService.applyRefund(userId, orderId, refundDTO);
        return ResponseResult.success();
    }

    /**
     * 获取用户退款申请列表
     *
     * @param status   退款状态(可选)
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds")
    public ResponseResult<Page<RefundVO>> getUserRefunds(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<RefundVO> refunds = orderService.getUserRefunds(userId, status, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取退款详情
     *
     * @param refundId 退款ID
     * @return 退款详情
     */
    @GetMapping("/refunds/{refundId}")
    public ResponseResult<RefundVO> getRefundDetail(@PathVariable Long refundId) {
        String userId = UserHolder.getCurrentUserId();
        RefundVO refund = orderService.getRefundDetail(userId, refundId);
        return ResponseResult.success(refund);
    }

    /**
     * 获取申请中的退款列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/applying")
    public ResponseResult<Page<RefundVO>> getApplyingRefunds(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<RefundVO> refunds = orderService.getUserRefunds(userId, 0, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已同意的退款列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/agreed")
    public ResponseResult<Page<RefundVO>> getAgreedRefunds(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<RefundVO> refunds = orderService.getUserRefunds(userId, 1, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已拒绝的退款列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/rejected")
    public ResponseResult<Page<RefundVO>> getRejectedRefunds(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<RefundVO> refunds = orderService.getUserRefunds(userId, 2, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已退款的列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/refunded")
    public ResponseResult<Page<RefundVO>> getRefundedOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<RefundVO> refunds = orderService.getUserRefunds(userId, 3, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }
}
