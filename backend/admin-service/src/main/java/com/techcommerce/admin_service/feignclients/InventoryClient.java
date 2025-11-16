package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "createInventoryFallback")
    @Retry(name = "inventoryService")
    InventoryDto createInventory(@RequestBody InventoryDto dto);

    default InventoryDto createInventoryFallback(InventoryDto dto, Throwable t) {
        return new InventoryDto();
    }

    @GetMapping("/api/inventory/{id}")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getInventoryFallback")
    @Retry(name = "inventoryService")
    InventoryDto getInventory(@PathVariable Long id);

    default InventoryDto getInventoryFallback(Long id, Throwable t) {
        return new InventoryDto();
    }

    @GetMapping("/api/inventory/product/{productId}")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getInventoryByProductFallback")
    @Retry(name = "inventoryService")
    InventoryDto getInventoryByProduct(@PathVariable Long productId);

    default InventoryDto getInventoryByProductFallback(Long productId, Throwable t) {
        return new InventoryDto();
    }

    @PutMapping("/api/inventory/{id}")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "updateInventoryFallback")
    @Retry(name = "inventoryService")
    InventoryDto updateInventory(@PathVariable Long id, @RequestBody InventoryDto dto);

    default InventoryDto updateInventoryFallback(Long id, InventoryDto dto, Throwable t) {
        return new InventoryDto();
    }

    @DeleteMapping("/api/inventory/{id}")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "deleteInventoryFallback")
    @Retry(name = "inventoryService")
    void deleteInventory(@PathVariable Long id);

    default void deleteInventoryFallback(Long id, Throwable t) {
        // noop
    }
}
