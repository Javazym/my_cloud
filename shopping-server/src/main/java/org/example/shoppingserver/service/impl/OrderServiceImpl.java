package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.model.dto.order.*;
import org.example.shoppingserver.model.entity.UserAddress;
import org.example.shoppingserver.model.vo.order.OrderVO;
import org.example.shoppingserver.model.vo.order.OrderItemVO;
import org.example.shoppingserver.model.vo.order.RefundVO;
import org.example.shoppingserver.model.vo.order.OrderStatisticsVO;
import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.model.entity.coupon.UserCoupon;
import org.example.shoppingserver.model.entity.merchant.Merchant;
import org.example.shoppingserver.model.entity.order.*;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.product.ProductSku;
import org.example.shoppingserver.mq.producer.OrderDelayProducer;
import org.example.shoppingserver.mq.producer.OrderProducer;
import org.example.shoppingserver.repository.*;
import org.example.shoppingserver.service.OrderService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ProductSkuRepository productSkuRepository;
    private final OrderProducer orderProducer;
    private final OrderDelayProducer orderDelayProducer;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductRepository productRepository;
    private final UserAddressRepository userAddressRepository;

    // ====================== 1. 创建订单（完整支持你的 DTO）======================

    @Transactional(rollbackFor = Exception.class)
    public void checkOrder(CreateOrderDTO dto) {
        for (CreateOrderDTO.OrderItemCreateDTO itemDto : dto.getItems()) {
            int stock = productSkuRepository.deductStock(itemDto.getSkuId(), 1);
            if (stock < 0) {
                throw new RuntimeException("商品库存不足");
            }
        }
        orderProducer.sendCreateOrderMessage(MessageWrapper.<CreateOrderDTO>builder()
                        .data(dto)
                        .targetService("order-service")
                .build());
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(String userId, CreateOrderDTO dto) {
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
        
        // 查询并设置收货地址
        UserAddress address = null;
        if (dto.getAddressId() != null) {
            address = userAddressRepository.findByUserIdAndId(userId, dto.getAddressId());
        }
        
        // 如果没有指定地址，使用默认地址
        if (address == null) {
            address = userAddressRepository.findByUserIdAndIsDefault(userId, 1)
                    .orElseThrow(() -> new RuntimeException("请添加收货地址"));
        }
        
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getFullAddress());
        // 2. 添加商品
        for (CreateOrderDTO.OrderItemCreateDTO itemDto : dto.getItems()) {
            OrderItem item = new OrderItem();

            // 查询商品信息（快照）
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + itemDto.getProductId()));
            
            // 查询SKU信息（如果有）
            ProductSku sku = null;
            if (itemDto.getSkuId() != null) {
                sku = productSkuRepository.findById(itemDto.getSkuId())
                        .orElseThrow(() -> new RuntimeException("SKU不存在: " + itemDto.getSkuId()));
            }

            item.setProduct(product);
            item.setSku(sku);
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            
            // 设置价格：优先使用SKU价格，否则使用商品价格
            BigDecimal price = sku != null ? sku.getPrice() : product.getPrice();
            item.setProductPrice(price);
            item.setQuantity(itemDto.getQuantity());
            
            // 计算总价
            item.calculateTotalPrice();
            
            item.setOrderNo(order.getOrderNo());
            item.setReviewStatus(0);

            order.addItem(item);
        }

        // 3. 计算订单总额
        order.recalculateTotal();
        
        // 4. 处理优惠券（在计算总额后）
        if (dto.getCouponId() != null) {
            UserCoupon coupon = userCouponRepository.findById(dto.getCouponId()).orElse(null);
            if (coupon != null && coupon.getStatus() == 0 && coupon.isAvailable()) {
                // 计算优惠金额
                BigDecimal discountAmount = coupon.calculateDiscount(order.getTotalAmount());
                order.setCouponAmount(discountAmount);
                
                // 更新优惠券状态
                coupon.setStatus(1);
                coupon.setUseTime(LocalDateTime.now());
                coupon.setOrder(order);
                
                // 重新计算实付金额
                order.recalculateTotal();
            }
        }
        Order savedOrder = orderRepository.save(order);
        orderDelayProducer.sendOrderTimeoutMessage(order.getId());
        return convertToVO(savedOrder);
    }

    // ====================== 2. 获取订单列表 ======================
    @Override
    public Page<OrderVO> getOrders(String userId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> page;

        if (status == null) {
            page = orderRepository.findByUserId(userId, pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.values()[status];
            page = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
        }

        return page.map(this::convertToVO);
    }

    // ====================== 3. 订单详情 ======================
    @Override
    @Cacheable(value = "orderDetail", key = "#orderId", unless = "#result == null")
    public OrderVO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToVO(order);
    }

    // ====================== 4. 订单详情 + 物流 ======================
    @Override
    @Cacheable(value = "orderDetailWithLogistics", key = "#orderId", unless = "#result == null")
    public OrderVO getOrderDetailWithLogistics(Long orderId) {
        OrderVO vo = getOrderDetail(orderId);
        orderLogisticsRepository.findByOrderId(orderId).ifPresent(log -> {
            OrderLogisticsDTO logisticsDTO = new OrderLogisticsDTO();
            logisticsDTO.setId(log.getId());
            logisticsDTO.setOrderId(log.getOrder().getId());
            logisticsDTO.setLogisticsCompany(log.getLogisticsCompany());
            logisticsDTO.setTrackingNumber(log.getTrackingNumber());
            logisticsDTO.setCurrentStatus(log.getCurrentStatus());
            logisticsDTO.setTraces(log.getTraces());
            vo.setLogistics(logisticsDTO);
        });
        return vo;
    }

    // ====================== 5. 取消订单 ======================
    @Override
    @Transactional
    @CacheEvict(value = {"orderDetail", "orderDetailWithLogistics"}, key = "#orderId")
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
    @CacheEvict(value = {"orderDetail", "orderDetailWithLogistics"}, key = "#orderId")
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
    @CacheEvict(value = {"orderDetail", "orderDetailWithLogistics"}, key = "#orderId")
    public boolean deleteOrder(String userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return false;

        orderRepository.delete(order);
        return true;
    }

    // ====================== 8. 支付订单 ======================
    @Override
    public String payOrder(String userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(userId)) return null;
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) return null;
        order.pay();
        orderRepository.save(order);
        return "mock-pay-success:" + orderId;
    }

    // ====================== 9. 支付回调 ======================
    @Override
    @Transactional
    @CacheEvict(value = {"orderDetail", "orderDetailWithLogistics"}, key = "#orderId")
    public boolean payCallback(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.PENDING_PAYMENT) return false;
        order.pay();
        orderRepository.save(order);
        return true;
    }

    // ====================== 10. 申请退款 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyRefund(String userId, Long orderId, RefundDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证订单是否属于该用户
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 验证订单状态：只有待收货和已完成的订单可以申请退款
        if (order.getStatus() != OrderStatus.PENDING_RECEIPT && order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("当前订单状态不允许申请退款");
        }

        // 验证是否已经有进行中的退款申请
        boolean hasPendingRefund = orderRefundRepository.existsByOrderIdAndStatusIn(
                orderId, 
                java.util.Arrays.asList(RefundStatus.APPLYING, RefundStatus.AGREED)
        );
        if (hasPendingRefund) {
            throw new RuntimeException("该订单已有进行中的退款申请");
        }

        // 验证退款金额不能超过订单实付金额
        if (dto.getAmount().compareTo(order.getPayAmount()) > 0) {
            throw new RuntimeException("退款金额不能超过订单实付金额");
        }

        // 创建退款申请
        OrderRefund refund = new OrderRefund();
        refund.setRefundNo(generateRefundNo());
        refund.setOrder(order);
        refund.setUser(order.getUser());
        refund.setMerchant(order.getMerchant());
        refund.setType(dto.getType());
        refund.setAmount(dto.getAmount());
        refund.setReason(dto.getReason());
        refund.setDescription(dto.getDescription());
        refund.setImages(dto.getImages());
        refund.setStatus(RefundStatus.APPLYING);

        orderRefundRepository.save(refund);
        return true;
    }

    // ====================== 10.5. 获取用户退款申请列表 ======================
    @Override
    public Page<RefundVO> getUserRefunds(String userId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<OrderRefund> page;

        if (status == null) {
            page = orderRefundRepository.findByUserId(userId, pageable);
        } else {
            RefundStatus refundStatus = RefundStatus.values()[status];
            page = orderRefundRepository.findByUserIdAndStatus(userId, refundStatus, pageable);
        }

        return page.map(this::convertToRefundVO);
    }

    // ====================== 10.6. 获取退款详情 ======================
    @Override
    public RefundVO getRefundDetail(String userId, Long refundId) {
        OrderRefund refund = orderRefundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款申请不存在"));
        
        // 验证是否属于该用户
        if (!refund.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权查看此退款申请");
        }
        
        return convertToRefundVO(refund);
    }

    // ====================== 11. 获取订单状态 ======================
    @Override
    @Cacheable(value = "orderStatus", key = "#orderId", unless = "#result == null")
    public Integer getOrderStatus(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getStatus().ordinal())
                .orElse(-1);
    }

    // ====================== 12. 获取商家订单列表 ======================
    @Override
    public Page<OrderVO> getMerchantOrders(Long merchantId, Integer status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Order> page;

        if (status == null) {
            page = orderRepository.findByMerchantId(merchantId, pageable);
        } else {
            OrderStatus orderStatus = OrderStatus.values()[status];
            page = orderRepository.findByMerchantIdAndStatus(merchantId, orderStatus, pageable);
        }

        return page.map(this::convertToVO);
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
    @Transactional(rollbackFor = Exception.class)
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

        // 更新退款状态为已同意
        refund.setStatus(RefundStatus.AGREED);
        orderRefundRepository.save(refund);
        
        // 执行实际退款操作
        processRefund(refund);
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
    public Page<RefundVO> getMerchantRefunds(Long merchantId, Integer status, int pageNum, int pageSize) {
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
    public OrderStatisticsVO getMerchantOrderStatistics(Long merchantId) {
        OrderStatisticsVO statistics = new OrderStatisticsVO();

        // 总订单数
        statistics.setTotalOrders(orderRepository.countOrdersByMerchantId(merchantId));

        // 各状态订单数
        statistics.setPendingPayment(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_PAYMENT));
        statistics.setPendingShipment(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_SHIPMENT));
        statistics.setPendingReceipt(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_RECEIPT));
        statistics.setCompleted(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.COMPLETED));
        statistics.setCancelled(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.CANCELLED));

        // 今日销售额
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        statistics.setTodaySales(orderRepository.sumTodaySales(todayStart));

        // 本月销售额
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        statistics.setMonthSales(orderRepository.sumTodaySales(monthStart));

        return statistics;
    }

    // ====================== 18. 获取商家各状态订单数量统计 ======================
    @Override
    public org.example.shoppingserver.model.vo.order.OrderStatusCountVO getOrderStatusCount(Long merchantId) {
        org.example.shoppingserver.model.vo.order.OrderStatusCountVO vo = new org.example.shoppingserver.model.vo.order.OrderStatusCountVO();

        // 总订单数
        vo.setTotalOrders(orderRepository.countOrdersByMerchantId(merchantId));

        // 各状态订单数
        vo.setPendingPayment(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_PAYMENT));
        vo.setPendingShipment(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_SHIPMENT));
        vo.setPendingReceipt(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.PENDING_RECEIPT));
        vo.setCompleted(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.COMPLETED));
        vo.setCancelled(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.CANCELLED));
        vo.setRefunded(orderRepository.countOrdersByMerchantIdAndStatus(merchantId, OrderStatus.REFUNDED));

        return vo;
    }

    // ====================== 工具方法 ======================
    
    /**
     * 处理退款操作
     * - 更新订单状态为已退款
     * - 恢复库存
     * - 恢复优惠券（如果有）
     */
    private void processRefund(OrderRefund refund) {
        Order order = refund.getOrder();
        
        // 1. 更新订单状态为已退款
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
        
        // 2. 恢复商品库存
        for (OrderItem item : order.getItems()) {
            if (item.getSku() != null) {
                // 恢复SKU库存
                ProductSku sku = item.getSku();
                sku.setStock(sku.getStock() + item.getQuantity());
                productSkuRepository.save(sku);
            } else {
                // 恢复商品库存
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
        
        // 3. 如果使用了优惠券，恢复优惠券
        if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            // 查找该订单使用的优惠券
            UserCoupon userCoupon = userCouponRepository.findByOrderId(order.getId()).orElse(null);
            if (userCoupon != null) {
                userCoupon.setStatus(0); // 恢复为未使用状态
                userCoupon.setUseTime(null);
                userCoupon.setOrder(null);
                userCouponRepository.save(userCoupon);
            }
        }
        
        // 4. 更新退款状态为已退款
        refund.setStatus(RefundStatus.REFUNDED);
        orderRefundRepository.save(refund);
    }
    
    private String generateOrderNo() {
        return "O" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
    }

    private String generateRefundNo() {
        return "R" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
    }

    // ====================== 🔴 绝对匹配你的 VO 转换器 ======================
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUser().getId());
        vo.setMerchantId(order.getMerchant().getId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setCouponAmount(order.getCouponAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setPoints(order.getPoints());
        vo.setStatus(order.getStatus().ordinal());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setPayTime(order.getPayTime());
        vo.setShipTime(order.getShipTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setFinishTime(order.getFinishTime());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setCreatedAt(order.getCreatedAt());

        // 订单商品（使用 OrderItemVO）
        List<OrderItemVO> itemVOList = order.getItems().stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setOrderId(order.getId());
            itemVO.setProductId(item.getProduct().getId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setSkuId(item.getSku() != null ? item.getSku().getId() : null);
            itemVO.setSkuSpecs(item.getSkuSpecs());
            itemVO.setProductPrice(item.getProductPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalPrice(item.getTotalPrice());
            itemVO.setReviewStatus(item.getReviewStatus());
            return itemVO;
        }).collect(Collectors.toList());

        vo.setItems(itemVOList);
        return vo;
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
    private RefundVO convertToRefundVO(OrderRefund refund) {
        RefundVO vo = new RefundVO();
        vo.setId(refund.getId());
        vo.setOrderNo(refund.getOrder().getOrderNo());
        vo.setOrderId(refund.getOrder().getId());
        vo.setUserId(refund.getUser().getId());
        vo.setUserName(refund.getUser().getUsername());
        vo.setRefundAmount(refund.getAmount());
        vo.setRefundType(refund.getType());
        vo.setReason(refund.getReason());
        vo.setDescription(refund.getDescription());
        vo.setImages(refund.getImages());
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