package com.techcommerce.order_service.feignclient;

import com.techcommerce.order_service.dto.PaymentRequest;
import com.techcommerce.order_service.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    @GetMapping("/api/payments/{transactionId}")
    PaymentResponse getPayment(@PathVariable String transactionId);

    @PostMapping("/api/payments/{paymentId}/refund")
    PaymentResponse refundPayment(@PathVariable Long paymentId);
}
