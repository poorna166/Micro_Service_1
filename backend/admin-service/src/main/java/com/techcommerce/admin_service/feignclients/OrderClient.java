package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "order-service")
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/status")
    OrderDTO updateStatus(@PathVariable Long orderId, @RequestParam String status);

    @GetMapping("/api/orders/{orderId}")
    OrderDTO getOrder(@PathVariable Long orderId);

 //   @GetMapping("/api/orders/stats/revenue")
    //RevenueDTO getRevenueStats();
}
