package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.PaymentDto;
import com.techcommerce.admin_service.feignclients.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class AdminPaymentService {

    private final PaymentClient paymentClient;

    public AdminPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentDto createPayment(PaymentDto dto) {
        return paymentClient.createPayment(dto);
    }

    public PaymentDto getPayment(Long id) {
        return paymentClient.getPayment(id);
    }

    public PaymentDto getPaymentByOrder(Long orderId) {
        return paymentClient.getPaymentByOrder(orderId);
    }

    public PaymentDto updatePayment(Long id, PaymentDto dto) {
        return paymentClient.updatePayment(id, dto);
    }

    public void deletePayment(Long id) {
        paymentClient.deletePayment(id);
    }
}
