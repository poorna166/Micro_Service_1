package com.techcommerce.inventory_service.controller;

import com.techcommerce.inventory_service.dto.InventoryDTO;
import com.techcommerce.inventory_service.entity.Inventory;
import com.techcommerce.inventory_service.mapper.InventoryMapper;
import com.techcommerce.inventory_service.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryController(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        Optional<InventoryDTO> inventory = inventoryRepository.findByProductId(productId)
                .map(inventoryMapper::toDTO);
        return inventory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createOrUpdateInventory(@RequestBody InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory.setUpdatedAt(java.time.LocalDateTime.now());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMapper.toDTO(savedInventory));
    }
}