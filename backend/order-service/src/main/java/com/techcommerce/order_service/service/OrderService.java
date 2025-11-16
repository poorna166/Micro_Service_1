package com.techcommerce.order_service.service;

import com.techcommerce.order_service.dto.CreateOrderRequest;
import com.techcommerce.order_service.dto.OrderDTO;
import com.techcommerce.order_service.dto.OrderItemDTO;
import com.techcommerce.order_service.entity.Order;
import com.techcommerce.order_service.entity.OrderItem;
import com.techcommerce.order_service.mapper.OrderMapper;
import com.techcommerce.order_service.repository.OrderRepository;
import com.techcommerce.order_service.repository.OrderItemRepository;
import com.techcommerce.order_service.feignclient.InventoryClient;
import com.techcommerce.order_service.feignclient.PaymentClient;
import com.techcommerce.order_service.event.OrderPlacedEvent;
import com.techcommerce.order_service.event.OrderCanceledEvent;
import com.techcommerce.order_service.publisher.OrderEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;
    private final OrderEventPublisher orderEventPublisher;

    private static final String ORDER_QUEUE = "order.placed.queue";
    private static final String ORDER_EXCHANGE = "order.exchange";
    private static final String ORDER_ROUTING_KEY = "order.placed";

    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       OrderMapper orderMapper,
                       InventoryClient inventoryClient,
                       PaymentClient paymentClient,
                       RabbitTemplate rabbitTemplate,
                       OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.rabbitTemplate = rabbitTemplate;
        this.orderEventPublisher = orderEventPublisher;
    }

    /**
     * Create a new order - orchestrates inventory check and payment
     */
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Validate inventory for all items
        for (OrderItemDTO itemDto : request.getOrderItems()) {
            boolean hasStock = inventoryClient.checkStock(itemDto.getProductId(), itemDto.getQuantity());
            if (!hasStock) {
                throw new RuntimeException("Insufficient stock for product: " + itemDto.getProductId());
            }
        }

        // Create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAddressId(request.getAddressId());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Add order items and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDto : request.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDto.getProductId());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getId());

        // Reserve inventory for all items
        for (OrderItem item : savedOrder.getOrderItems()) {
            try {
                inventoryClient.reserveStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.warn("Failed to reserve stock for product {}: {}", item.getProductId(), e.getMessage());
            }
        }

        // Publish OrderPlacedEvent
        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderItems().stream()
                        .map(item -> new OrderPlacedEvent.OrderItem(item.getProductId(), item.getQuantity()))
                        .collect(Collectors.toList())
        );
        publishOrderEvent(event);

        return orderMapper.toDTO(savedOrder);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return orderMapper.toDTO(order);
    }

    /**
     * Get all orders for a user (paginated)
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size) {
        Page<Order> orders = orderRepository.findByUserId(userId, PageRequest.of(page, size));
        return orders.map(orderMapper::toDTO);
    }

    /**
     * Update order status
     */
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setOrderStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order status updated: {} -> {}", orderId, status);
        return orderMapper.toDTO(updated);
    }

    /**
     * Cancel order and release inventory
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!"PENDING".equals(order.getOrderStatus()) && !"CONFIRMED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus("CANCELED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Release inventory
        for (OrderItem item : order.getOrderItems()) {
            try {
                inventoryClient.releaseStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.warn("Failed to release stock for product {}: {}", item.getProductId(), e.getMessage());
            }
        }

        // Publish OrderCanceledEvent
        OrderCanceledEvent event = new OrderCanceledEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount()
        );
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, "order.canceled", event);
        log.info("Order canceled: {}", orderId);
    }

    /**
     * Publish order event to RabbitMQ
     */
    private void publishOrderEvent(OrderPlacedEvent event) {
        try {
            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, event);
            log.info("Order placed event published: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish order event: {}", e.getMessage());
        }
    }
}
