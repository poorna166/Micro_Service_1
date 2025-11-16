package com.techcommerce.admin_service.controller;

import com.techcommerce.admin_service.dto.InventoryDto;
import com.techcommerce.admin_service.service.AdminInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inventory")
@Tag(name = "Admin Inventory", description = "Admin operations for inventory management")
public class AdminInventoryController {

    private final AdminInventoryService service;

    public AdminInventoryController(AdminInventoryService service) {
        this.service = service;
    }

    @Operation(summary = "Create inventory record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventory created",
                    content = @Content(schema = @Schema(implementation = InventoryDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody InventoryDto dto) {
        return ResponseEntity.status(201).body(service.createInventory(dto));
    }

    @Operation(summary = "Get inventory by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory found"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInventory(id));
    }

    @Operation(summary = "Get inventory by product ID")
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getInventoryByProduct(productId));
    }

    @Operation(summary = "Update inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory updated"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InventoryDto dto) {
        return ResponseEntity.ok(service.updateInventory(id, dto));
    }

    @Operation(summary = "Delete inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Inventory deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
