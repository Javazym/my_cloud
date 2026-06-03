package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.order.CreateOrderDTO;
import org.example.shoppingserver.model.dto.order.RefundDTO;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.order.RefundVO;
import org.example.shoppingserver.model.dto.order.OrderLogisticsDTO;
import org.example.shoppingserver.model.vo.order.OrderStatisticsVO;
import org.springframework.data.domain.Page;

/**
 * 订单Service接口
 */
public interface OrderService {


    /**
     * @param dto
     * @return 订单信息
     */
    OrderVO checkOrder(CreateOrderDTO dto);

    /**
     * 创建订单
     *
     * @param userId        用户ID
     * @param createOrderDTO 创建订单DTO
     */
    void createOrder(String userId, CreateOrderDTO createOrderDTO);

    /**
     * 秒杀订单创建（异步）
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param skuId SKU ID
     * @param createOrderDTO 创建订单DTO
     * @return 订单信息（订单创建中）
     */
    OrderVO createSeckillOrder(String userId, Long activityId, Long skuId, CreateOrderDTO createOrderDTO);

    /**
     * 获取订单列表（用户端）
     *
     * @param userId   用户ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    Page<OrderVO> getOrders(String userId, Integer status, int pageNum, int pageSize);

    /**
     * 获取订单列表（商家端）
     *
     * @param merchantId 商家ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 订单分页结果
     */
    Page<OrderVO> getMerchantOrders(Long merchantId, Integer status, int pageNum, int pageSize);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 获取订单详情（包含完整物流信息）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetailWithLogistics(Long orderId);

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
     * @return 支付结果
     */
    String payOrder(String userId, Long orderId);

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
     * 获取用户退款申请列表
     *
     * @param userId   用户ID
     * @param status   退款状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 退款申请分页结果
     */
    Page<RefundVO> getUserRefunds(String userId, Integer status, int pageNum, int pageSize);

    /**
     * 获取退款详情
     *
     * @param userId   用户ID
     * @param refundId 退款ID
     * @return 退款详情
     */
    RefundVO getRefundDetail(String userId, Long refundId);

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
    Page<RefundVO> getMerchantRefunds(Long merchantId, Integer status, int pageNum, int pageSize);

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
    OrderStatisticsVO getMerchantOrderStatistics(Long merchantId);

    /**
     * 获取商家各状态订单数量统计
     *
     * @param merchantId 商家ID
     * @return 订单状态统计
     */
    org.example.shoppingserver.model.vo.order.OrderStatusCountVO getOrderStatusCount(Long merchantId);

}