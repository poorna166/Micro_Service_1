package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.OrderDTO;
import com.techcommerce.admin_service.feignclients.OrderClient;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderService {

    private final OrderClient orderClient;

    public AdminOrderService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public OrderDTO updateOrderStatus(Long id, String status) {
        return orderClient.updateStatus(id, status);
    }
}
