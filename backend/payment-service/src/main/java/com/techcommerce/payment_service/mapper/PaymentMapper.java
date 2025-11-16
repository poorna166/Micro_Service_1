package com.techcommerce.payment_service.mapper;

import com.techcommerce.payment_service.dto.PaymentDTO;
import com.techcommerce.payment_service.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setPaymentGateway(payment.getPaymentGateway());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    public Payment toEntity(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setOrderId(dto.getOrderId());
        payment.setPaymentGateway(dto.getPaymentGateway());
        payment.setTransactionId(dto.getTransactionId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setCreatedAt(dto.getCreatedAt());
        return payment;
    }
}