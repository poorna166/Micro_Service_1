package com.techcommerce.order_service.mapper;

import com.techcommerce.order_service.dto.OrderDTO;
import com.techcommerce.order_service.dto.OrderItemDTO;
import com.techcommerce.order_service.entity.Order;
import com.techcommerce.order_service.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setAddressId(order.getAddressId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderItems(order.getOrderItems().stream().map(this::toDTO).collect(Collectors.toList()));
        return dto;
    }

    public OrderItemDTO toDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}