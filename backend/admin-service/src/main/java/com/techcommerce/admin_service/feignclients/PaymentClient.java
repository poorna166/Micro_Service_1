package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "createPaymentFallback")
    @Retry(name = "paymentService")
    PaymentDto createPayment(@RequestBody PaymentDto dto);

    default PaymentDto createPaymentFallback(PaymentDto dto, Throwable t) { return new PaymentDto(); }

    @GetMapping("/api/payments/{id}")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "getPaymentFallback")
    @Retry(name = "paymentService")
    PaymentDto getPayment(@PathVariable Long id);

    default PaymentDto getPaymentFallback(Long id, Throwable t) { return new PaymentDto(); }

    @GetMapping("/api/payments/order/{orderId}")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "getPaymentByOrderFallback")
    @Retry(name = "paymentService")
    PaymentDto getPaymentByOrder(@PathVariable Long orderId);

    default PaymentDto getPaymentByOrderFallback(Long orderId, Throwable t) { return new PaymentDto(); }

    @PutMapping("/api/payments/{id}")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "updatePaymentFallback")
    @Retry(name = "paymentService")
    PaymentDto updatePayment(@PathVariable Long id, @RequestBody PaymentDto dto);

    default PaymentDto updatePaymentFallback(Long id, PaymentDto dto, Throwable t) { return new PaymentDto(); }

    @DeleteMapping("/api/payments/{id}")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "deletePaymentFallback")
    @Retry(name = "paymentService")
    void deletePayment(@PathVariable Long id);

    default void deletePaymentFallback(Long id, Throwable t) { }
}
