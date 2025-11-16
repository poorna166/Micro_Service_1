package com.techcommerce.admin_service.controller;

import com.techcommerce.admin_service.dto.OrderDTO;
import com.techcommerce.admin_service.feignclients.OrderClient;
import com.techcommerce.admin_service.service.AdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService service;

    public AdminOrderController(AdminOrderService service) {
        this.service = service;
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(service.updateOrderStatus(id, status));
    }
}
