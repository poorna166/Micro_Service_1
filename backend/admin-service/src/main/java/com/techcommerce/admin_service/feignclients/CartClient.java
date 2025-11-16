package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "cart-service")
public interface CartClient {

    @PostMapping("/api/carts")
    @CircuitBreaker(name = "cartService", fallbackMethod = "createCartFallback")
    @Retry(name = "cartService")
    CartDto createCart(@RequestBody CartDto dto);

    default CartDto createCartFallback(CartDto dto, Throwable t) { return new CartDto(); }

    @GetMapping("/api/carts/{id}")
    @CircuitBreaker(name = "cartService", fallbackMethod = "getCartFallback")
    @Retry(name = "cartService")
    CartDto getCart(@PathVariable Long id);

    default CartDto getCartFallback(Long id, Throwable t) { return new CartDto(); }

    @PutMapping("/api/carts/{id}")
    @CircuitBreaker(name = "cartService", fallbackMethod = "updateCartFallback")
    @Retry(name = "cartService")
    CartDto updateCart(@PathVariable Long id, @RequestBody CartDto dto);

    default CartDto updateCartFallback(Long id, CartDto dto, Throwable t) { return new CartDto(); }

    @DeleteMapping("/api/carts/{id}")
    @CircuitBreaker(name = "cartService", fallbackMethod = "deleteCartFallback")
    @Retry(name = "cartService")
    void deleteCart(@PathVariable Long id);

    default void deleteCartFallback(Long id, Throwable t) { }
}
