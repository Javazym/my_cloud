package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.order.OrderLogisticsDTO;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.order.RefundVO;
import org.example.shoppingserver.model.vo.order.OrderStatisticsVO;
import org.example.shoppingserver.model.vo.order.OrderStatusCountVO;
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
     * @param merchantId 商家ID
     * @param status   订单状态(可选)
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    @GetMapping
    public ResponseResult<Page<OrderVO>> getMerchantOrders(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<OrderVO> orders = orderService.getMerchantOrders(merchantId, status, pageNum, pageSize);
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
        orderService.shipOrder(logisticsDTO.getMerchantId(), orderId, logisticsDTO);
        return ResponseResult.success();
    }

    /**
     * 同意退款申请
     *
     * @param merchantId 商家ID
     * @param refundId 退款ID
     * @return 操作结果
     */
    @PutMapping("/refunds/{refundId}/approve")
    public ResponseResult<Void> approveRefund(
            @RequestParam Long merchantId,
            @PathVariable Long refundId) {
        orderService.approveRefund(merchantId, refundId);
        return ResponseResult.success();
    }

    /**
     * 拒绝退款申请
     *
     * @param merchantId 商家ID
     * @param refundId 退款ID
     * @param reason 拒绝原因
     * @return 操作结果
     */
    @PutMapping("/refunds/{refundId}/reject")
    public ResponseResult<Void> rejectRefund(
            @RequestParam Long merchantId,
            @PathVariable Long refundId,
            @RequestParam String reason) {
        orderService.rejectRefund(merchantId, refundId, reason);
        return ResponseResult.success();
    }

    /**
     * 获取退款申请列表
     *
     * @param merchantId 商家ID
     * @param status 退款状态(可选)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds")
    public ResponseResult<Page<RefundVO>> getRefundApplications(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<RefundVO> refunds = orderService.getMerchantRefunds(merchantId, status, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取申请中的退款列表（待审核）
     *
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/applying")
    public ResponseResult<Page<RefundVO>> getApplyingRefunds(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<RefundVO> refunds = orderService.getMerchantRefunds(merchantId, 0, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已同意的退款列表
     *
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/agreed")
    public ResponseResult<Page<RefundVO>> getAgreedRefunds(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<RefundVO> refunds = orderService.getMerchantRefunds(merchantId, 1, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已拒绝的退款列表
     *
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/rejected")
    public ResponseResult<Page<RefundVO>> getRejectedRefunds(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<RefundVO> refunds = orderService.getMerchantRefunds(merchantId, 2, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取已退款的列表
     *
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    @GetMapping("/refunds/status/refunded")
    public ResponseResult<Page<RefundVO>> getRefundedRefunds(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<RefundVO> refunds = orderService.getMerchantRefunds(merchantId, 3, pageNum, pageSize);
        return ResponseResult.success(refunds);
    }

    /**
     * 获取订单统计信息
     *
     * @param merchantId 商家ID
     * @return 订单统计
     */
    @GetMapping("/statistics")
    public ResponseResult<OrderStatisticsVO> getOrderStatistics(
            @RequestParam Long merchantId) {
        OrderStatisticsVO statistics = orderService.getMerchantOrderStatistics(merchantId);
        return ResponseResult.success(statistics);
    }

    /**
     * 获取各状态订单数量统计
     *
     * @param merchantId 商家ID
     * @return 订单状态统计
     */
    @GetMapping("/status-count")
    public ResponseResult<OrderStatusCountVO> getOrderStatusCount(
            @RequestParam Long merchantId) {
        OrderStatusCountVO statusCount = orderService.getOrderStatusCount(merchantId);
        return ResponseResult.success(statusCount);
    }
}
