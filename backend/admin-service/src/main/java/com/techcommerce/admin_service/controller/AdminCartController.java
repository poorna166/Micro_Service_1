package com.techcommerce.admin_service.controller;

import com.techcommerce.admin_service.dto.CartDto;
import com.techcommerce.admin_service.service.AdminCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/carts")
@Tag(name = "Admin Carts", description = "Admin operations for cart management")
public class AdminCartController {

    private final AdminCartService service;

    public AdminCartController(AdminCartService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart created",
                    content = @Content(schema = @Schema(implementation = CartDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CartDto dto) {
        return ResponseEntity.status(201).body(service.createCart(dto));
    }

    @Operation(summary = "Get cart by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart found"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCart(id));
    }

    @Operation(summary = "Update an existing cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart updated"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CartDto dto) {
        return ResponseEntity.ok(service.updateCart(id, dto));
    }

    @Operation(summary = "Delete cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart deleted"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
