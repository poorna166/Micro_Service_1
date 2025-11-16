package com.techcommerce.inventory_service.mapper;

import com.techcommerce.inventory_service.dto.InventoryDTO;
import com.techcommerce.inventory_service.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryDTO toDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setAvailableStock(inventory.getAvailableStock());
        dto.setReservedStock(inventory.getReservedStock());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        return dto;
    }

    public Inventory toEntity(InventoryDTO dto) {
        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());
        inventory.setProductId(dto.getProductId());
        inventory.setAvailableStock(dto.getAvailableStock());
        inventory.setReservedStock(dto.getReservedStock());
        inventory.setUpdatedAt(dto.getUpdatedAt());
        return inventory;
    }
}