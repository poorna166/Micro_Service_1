package com.techcommerce.order_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}/check")
    boolean checkStock(@PathVariable Long productId, @RequestParam Integer quantity);

    @PostMapping("/api/inventory/{productId}/reserve")
    void reserveStock(@PathVariable Long productId, @RequestParam Integer quantity);

    @PostMapping("/api/inventory/{productId}/release")
    void releaseStock(@PathVariable Long productId, @RequestParam Integer quantity);
}
