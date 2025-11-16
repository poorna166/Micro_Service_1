package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;


@FeignClient(name = "order-service")
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/status")
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateStatusFallback")
    @Retry(name = "orderService")
    OrderDTO updateStatus(@PathVariable Long orderId, @RequestParam String status);

    default OrderDTO updateStatusFallback(Long orderId, String status, Throwable t) {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderId);
        dto.setOrderStatus("UNKNOWN");
        return dto;
    }

    @GetMapping("/api/orders/{orderId}")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    OrderDTO getOrder(@PathVariable Long orderId);

    default OrderDTO getOrderFallback(Long orderId, Throwable t) {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderId);
        dto.setOrderStatus("UNAVAILABLE");
        return dto;
    }

 //   @GetMapping("/api/orders/stats/revenue")
    //RevenueDTO getRevenueStats();
}
