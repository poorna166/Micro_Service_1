package com.techcommerce.payment_service.controller;

import com.techcommerce.payment_service.dto.PaymentDTO;
import com.techcommerce.payment_service.entity.Payment;
import com.techcommerce.payment_service.mapper.PaymentMapper;
import com.techcommerce.payment_service.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        Optional<PaymentDTO> payment = paymentRepository.findByTransactionId(transactionId)
                .map(paymentMapper::toDTO);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setCreatedAt(java.time.LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDTO(savedPayment));
    }
}