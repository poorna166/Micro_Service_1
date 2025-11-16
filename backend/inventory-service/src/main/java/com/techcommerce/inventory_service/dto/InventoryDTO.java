package com.techcommerce.inventory_service.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class InventoryDTO {
    private Long id;
    private Long productId;
    private Integer availableStock;
    private Integer reservedStock;
    private LocalDateTime updatedAt;

    
}