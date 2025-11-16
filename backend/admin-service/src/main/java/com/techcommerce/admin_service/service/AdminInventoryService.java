package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.InventoryDto;
import com.techcommerce.admin_service.feignclients.InventoryClient;
import org.springframework.stereotype.Service;

@Service
public class AdminInventoryService {

    private final InventoryClient inventoryClient;

    public AdminInventoryService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public InventoryDto createInventory(InventoryDto dto) {
        return inventoryClient.createInventory(dto);
    }

    public InventoryDto getInventory(Long id) {
        return inventoryClient.getInventory(id);
    }

    public InventoryDto getInventoryByProduct(Long productId) {
        return inventoryClient.getInventoryByProduct(productId);
    }

    public InventoryDto updateInventory(Long id, InventoryDto dto) {
        return inventoryClient.updateInventory(id, dto);
    }

    public void deleteInventory(Long id) {
        inventoryClient.deleteInventory(id);
    }
}
