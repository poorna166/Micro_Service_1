package com.techcommerce.admin_service.controller;

import com.techcommerce.admin_service.dto.PaymentDto;
import com.techcommerce.admin_service.service.AdminPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/payments")
@Tag(name = "Admin Payments", description = "Admin operations for payment management")
public class AdminPaymentController {

    private final AdminPaymentService service;

    public AdminPaymentController(AdminPaymentService service) {
        this.service = service;
    }

    @Operation(summary = "Create payment record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentDto dto) {
        return ResponseEntity.status(201).body(service.createPayment(dto));
    }

    @Operation(summary = "Get payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPayment(id));
    }

    @Operation(summary = "Get payment by order ID")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getPaymentByOrder(orderId));
    }

    @Operation(summary = "Update payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(service.updatePayment(id, dto));
    }

    @Operation(summary = "Delete payment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
