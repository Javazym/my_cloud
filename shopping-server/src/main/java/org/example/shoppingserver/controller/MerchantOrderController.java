package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.OrderDTO;
import org.example.shoppingserver.model.dto.OrderLogisticsDTO;
import org.example.shoppingserver.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 商家订单控制器
 */
@RestController
@RequestMapping("/merchant/orders")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;

    /**
     * 获取商家订单列表
     *
     * @param status   订单状态(可选)
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    @GetMapping
    public ResponseResult<Page<OrderDTO>> getMerchantOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long merchantId = UserHolder.getCurrentMerchantId();
        Page<OrderDTO> orders = orderService.getMerchantOrders(merchantId, status, pageNum, pageSize);
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
     * 发货
     *
     * @param orderId 订单ID
     * @param logisticsDTO 物流信息
     * @return 操作结果
     */
    @PutMapping("/{orderId}/ship")
    public ResponseResult<Void> shipOrder(
            @PathVariable Long orderId,
            @RequestBody OrderLogisticsDTO logisticsDTO) {
        Long merchantId = UserHolder.getCurrentMerchantId();
        orderService.shipOrder(merchantId, orderId, logisticsDTO);
        return ResponseResult.success();
    }

    /**
     * 同意退款申请
     *
     * @param refundId 退款ID
     * @return 操作结果
     */
    @PutMapping("/refunds/{refundId}/approve")
    public ResponseResult<Void> approveRefund(@PathVariable Long refundId) {
        Long merchantId = UserHolder.getCurrentMerchantId();
        orderService.approveRefund(merchantId, refundId);
        return ResponseResult.success();
    }

    /**
     * 拒绝退款申请
     *
     * @param refundId 退款ID
     * @param reason 拒绝原因
     * @return 操作结果
     */
    @PutMapping("/refunds/{refundId}/reject")
    public ResponseResult<Void> rejectRefund(
            @PathVariable Long refundId,
            @RequestParam String reason) {
        Long merchantId = UserHolder.getCurrentMerchantId();
        orderService.rejectRefund(merchantId, refundId, reason);
        return ResponseResult.success();
    }

    /**
     * 获取退款申请列表
     *
     * @param status 退款状态(可选)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds")
    public ResponseResult<Page<OrderDTO.RefundVO>> getRefundApplications(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long merchantId = UserHolder.getCurrentMerchantId();
        Page<OrderDTO.RefundVO> refunds = orderService.getMerchantRefunds(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取订单统计信息
     *
     * @return 订单统计
     */
    @GetMapping("/statistics")
    public ResponseResult<OrderService.OrderStatistics> getOrderStatistics() {
        Long merchantId = UserHolder.getCurrentMerchantId();
        OrderService.OrderStatistics statistics = orderService.getMerchantOrderStatistics(merchantId);
        return ResponseResult.success(statistics);
    }
}
