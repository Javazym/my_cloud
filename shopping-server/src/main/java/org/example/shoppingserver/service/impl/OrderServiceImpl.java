package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.entity.OrderStatus;
import org.example.shoppingserver.model.entity.RefundStatus;
import org.example.shoppingserver.model.dto.CreateOrderDTO;
import org.example.shoppingserver.model.dto.OrderDTO;
import org.example.shoppingserver.model.dto.OrderItemDTO;
import org.example.shoppingserver.model.dto.OrderLogisticsDTO;
import org.example.shoppingserver.model.entity.Merchant;
import org.example.shoppingserver.model.entity.Order;
import org.example.shoppingserver.model.entity.OrderItem;
import org.example.shoppingserver.model.entity.OrderLogistics;
import org.example.shoppingserver.model.entity.OrderRefund;
import org.example.shoppingserver.model.entity.Product;
import org.example.shoppingserver.model.entity.ProductSku;
import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.repository.OrderItemRepository;
import org.example.shoppingserver.repository.OrderLogisticsRepository;
import org.example.shoppingserver.repository.OrderRefundRepository;
import org.example.shoppingserver.repository.OrderRepository;
import org.example.shoppingserver.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderLogisticsRepository orderLogisticsRepository;
    private final OrderRefundRepository orderRefundRepository;

    // ====================== 1. 创建订单（完整支持你的 DTO）======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(String userId, CreateOrderDTO dto) {
        User user = new User();
        user.setId(userId);

        Merchant merchant = new Merchant();
        merchant.setId(dto.getMerchantId());

        // 1. 构建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setMerchant(merchant);
        order.setRemark(dto.getRemark());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        // 2. 添加商品
        for (CreateOrderDTO.OrderItemCreateDTO itemDto : dto.getItems()) {
            OrderItem item = new OrderItem();

            Product product = new Product();
            product.setId(itemDto.getProductId());

            ProductSku sku = new ProductSku();
            sku.setId(itemDto.getSkuId());

            item.setProduct(product);
            item.setSku(sku);
            item.setQuantity(itemDto.getQuantity());
            item.setOrderNo(order.getOrderNo());
            item.setReviewStatus(0);

            order.addItem(item);
        }

        // 3. 计算总价
        order.recalculateTotal();
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    // ====================== 2. 获取订单列表 ======================
    @Override
    public Page<OrderDTO> getOrders(String userId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> page;

        if (status == null) {
            page = orderRepository.findByUserId(userId, pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.values()[status];
            page = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
        }

        return page.map(this::convertToDTO);
    }

    // ====================== 3. 订单详情 ======================
    @Override
    public OrderDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }

    // ====================== 4. 订单详情 + 物流 ======================
    @Override
    public OrderDTO getOrderDetailWithLogistics(Long orderId) {
        OrderDTO dto = getOrderDetail(orderId);
        orderLogisticsRepository.findByOrderId(orderId).ifPresent(log -> {
            OrderLogisticsDTO logisticsDTO = new OrderLogisticsDTO();
            logisticsDTO.setId(log.getId());
            logisticsDTO.setOrderId(log.getOrder().getId());
            logisticsDTO.setLogisticsCompany(log.getLogisticsCompany());
            logisticsDTO.setTrackingNumber(log.getTrackingNumber());
            logisticsDTO.setCurrentStatus(log.getCurrentStatus());
            logisticsDTO.setTraces(log.getTraces());
            dto.setLogistics(logisticsDTO);
        });
        return dto;
    }

    // ====================== 5. 取消订单 ======================
    @Override
    @Transactional
    public boolean cancelOrder(String userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return false;
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) return false;

        order.cancel();
        orderRepository.save(order);
        return true;
    }

    // ====================== 6. 确认收货 ======================
    @Override
    @Transactional
    public boolean confirmReceipt(String userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return false;
        if (order.getStatus() != OrderStatus.PENDING_RECEIPT) return false;

        order.confirmReceipt();
        orderRepository.save(order);
        return true;
    }

    // ====================== 7. 删除订单 ======================
    @Override
    @Transactional
    public boolean deleteOrder(String userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return false;

        orderRepository.delete(order);
        return true;
    }

    // ====================== 8. 支付订单 ======================
    @Override
    public String payOrder(String userId, Long orderId, String payType) {
        return "mock-pay-success:" + orderId;
    }

    // ====================== 9. 支付回调 ======================
    @Override
    @Transactional
    public boolean payCallback(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.PENDING_PAYMENT) return false;

        order.pay();
        orderRepository.save(order);
        return true;
    }

    // ====================== 10. 申请退款 ======================
    @Override
    @Transactional
    public boolean applyRefund(String userId, Long orderId, RefundDTO dto) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return false;

        OrderRefund refund = new OrderRefund();
        refund.setRefundNo(generateRefundNo());
        refund.setOrder(order);
        refund.setUser(order.getUser());
        refund.setMerchant(order.getMerchant());
        refund.setType(dto.getType());
        refund.setAmount(dto.getAmount());
        refund.setReason(dto.getReason());
        refund.setDescription(dto.getDescription());
        refund.setStatus(RefundStatus.APPLYING);

        orderRefundRepository.save(refund);
        return true;
    }

    // ====================== 11. 获取订单状态 ======================
    @Override
    public Integer getOrderStatus(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getStatus().ordinal())
                .orElse(-1);
    }

    // ====================== 12. 获取商家订单列表 ======================
    @Override
    public Page<OrderDTO> getMerchantOrders(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> page;

        if (status == null) {
            page = orderRepository.findByMerchantId(merchantId, pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.values()[status];
            page = orderRepository.findByMerchantIdAndStatus(merchantId, orderStatus, pageable);
        }

        return page.map(this::convertToDTO);
    }

    // ====================== 13. 发货（商家端）======================
    @Override
    @Transactional
    public void shipOrder(Long merchantId, Long orderId, OrderLogisticsDTO logisticsDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 验证订单是否属于该商家
        if (!order.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT) {
            throw new RuntimeException("订单状态不允许发货");
        }

        // 创建物流信息
        OrderLogistics logistics = new OrderLogistics();
        logistics.setOrder(order);
        logistics.setLogisticsCompany(logisticsDTO.getLogisticsCompany());
        logistics.setTrackingNumber(logisticsDTO.getTrackingNumber());
        logistics.setCurrentStatus("已发货");

        // 添加物流轨迹（JSON字符串格式）
        String traces = "[{\"time\":\"" + LocalDateTime.now() + "\",\"status\":\"已发货\",\"description\":\"商家已发货，物流公司：" + logisticsDTO.getLogisticsCompany() + "\"}]";
        logistics.setTraces(traces);

        orderLogisticsRepository.save(logistics);

        // 更新订单状态
        order.ship();
        orderRepository.save(order);
    }

    // ====================== 14. 同意退款申请（商家端）======================
    @Override
    @Transactional
    public void approveRefund(Long merchantId, Long refundId) {
        OrderRefund refund = orderRefundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款申请不存在"));

        // 验证退款是否属于该商家
        if (!refund.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此退款申请");
        }

        // 验证退款状态
        if (refund.getStatus() != RefundStatus.APPLYING) {
            throw new RuntimeException("退款申请状态不正确");
        }

        // 更新退款状态
        refund.setStatus(RefundStatus.AGREED);
        orderRefundRepository.save(refund);
    }

    // ====================== 15. 拒绝退款申请（商家端）======================
    @Override
    @Transactional
    public void rejectRefund(Long merchantId, Long refundId, String reason) {
        OrderRefund refund = orderRefundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款申请不存在"));

        // 验证退款是否属于该商家
        if (!refund.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException("无权操作此退款申请");
        }

        // 验证退款状态
        if (refund.getStatus() != RefundStatus.APPLYING) {
            throw new RuntimeException("退款申请状态不正确");
        }

        // 更新退款状态
        refund.setStatus(RefundStatus.REJECTED);
        refund.setRejectReason(reason);
        orderRefundRepository.save(refund);
    }

    // ====================== 16. 获取商家退款申请列表 ======================
    @Override
    public Page<OrderDTO.RefundVO> getMerchantRefunds(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<OrderRefund> page;

        if (status == null) {
            page = orderRefundRepository.findByMerchantId(merchantId, pageable);
        } else {
            RefundStatus refundStatus = RefundStatus.values()[status];
            page = orderRefundRepository.findByMerchantIdAndStatus(merchantId, refundStatus, pageable);
        }

        return page.map(this::convertToRefundVO);
    }

    // ====================== 17. 获取商家订单统计信息 ======================
    @Override
    public OrderStatistics getMerchantOrderStatistics(Long merchantId) {
        OrderStatistics statistics = new OrderStatistics();

        // 总订单数
        statistics.setTotalOrders(orderRepository.countByMerchantId(merchantId));

        // 各状态订单数
        statistics.setPendingPayment(orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_PAYMENT));
        statistics.setPendingShipment(orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_SHIPMENT));
        statistics.setPendingReceipt(orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_RECEIPT));
        statistics.setCompleted(orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.COMPLETED));
        statistics.setCancelled(orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatus.CANCELLED));

        // 今日销售额
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        statistics.setTodaySales(orderRepository.sumTodaySales(todayStart));

        // 本月销售额
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        statistics.setMonthSales(orderRepository.sumTodaySales(monthStart));

        return statistics;
    }

    // ====================== 工具方法 ======================
    private String generateOrderNo() {
        return "O" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
    }

    private String generateRefundNo() {
        return "R" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
    }

    // ====================== 🔴 绝对匹配你的 DTO 转换器 ======================
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUser().getId());
        dto.setMerchantId(order.getMerchant().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setCouponAmount(order.getCouponAmount());
        dto.setFreightAmount(order.getFreightAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setPoints(order.getPoints());
        dto.setStatus(order.getStatus().ordinal());
        dto.setStatusText(getStatusText(order.getStatus()));
        dto.setPayTime(order.getPayTime());
        dto.setShipTime(order.getShipTime());
        dto.setReceiveTime(order.getReceiveTime());
        dto.setFinishTime(order.getFinishTime());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setRemark(order.getRemark());
        dto.setCreatedAt(order.getCreatedAt());

        // 订单商品（完全匹配 OrderItemDTO）
        List<OrderItemDTO> itemDTOList = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setOrderId(order.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setProductImage(item.getProductImage());
            itemDTO.setSkuId(item.getSku() != null ? item.getSku().getId() : null);
            itemDTO.setSkuSpecs(item.getSkuSpecs());
            itemDTO.setProductPrice(item.getProductPrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setTotalPrice(item.getTotalPrice());
            itemDTO.setReviewStatus(item.getReviewStatus());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOList);
        return dto;
    }

    private String getStatusText(OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "待付款";
            case PENDING_SHIPMENT -> "待发货";
            case PENDING_RECEIPT -> "待收货";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
            default -> "未知状态";
        };
    }

    /**
     * 转换退款Entity为RefundVO
     */
    private OrderDTO.RefundVO convertToRefundVO(OrderRefund refund) {
        OrderDTO.RefundVO vo = new OrderDTO.RefundVO();
        vo.setId(refund.getId());
        vo.setOrderNo(refund.getOrder().getOrderNo());
        vo.setOrderId(refund.getOrder().getId());
        vo.setUserId(refund.getUser().getId());
        vo.setUserName(refund.getUser().getUsername());
        vo.setRefundAmount(refund.getAmount());
        vo.setRefundType(refund.getType());
        vo.setReason(refund.getReason());
        vo.setDescription(refund.getDescription());
        vo.setStatus(refund.getStatus().ordinal());
        vo.setStatusText(getRefundStatusText(refund.getStatus()));
        vo.setApplyTime(refund.getCreatedAt());
        vo.setRejectReason(refund.getRejectReason());
        return vo;
    }

    private String getRefundStatusText(RefundStatus status) {
        return switch (status) {
            case APPLYING -> "待审核";
            case AGREED -> "已同意";
            case REJECTED -> "已拒绝";
            case REFUNDED -> "已退款";
            default -> "未知状态";
        };
    }
}