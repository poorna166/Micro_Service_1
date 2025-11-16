package com.techcommerce.inventory_service.service;

import com.techcommerce.inventory_service.dto.InventoryDTO;
import com.techcommerce.inventory_service.entity.Inventory;
import com.techcommerce.inventory_service.exception.InsufficientStockException;
import com.techcommerce.inventory_service.exception.ResourceNotFoundException;
import com.techcommerce.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing inventory operations including stock tracking,
 * reservations, and releases. Handles all business logic for inventory management.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * Get inventory details for a product
     */
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryByProductId(Long productId) {
        log.info("Fetching inventory for product: {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        return inventoryMapper.toDTO(inventory);
    }

    /**
     * Check if sufficient stock is available for a product
     */
    @Transactional(readOnly = true)
    public boolean checkStock(Long productId, Integer quantity) {
        log.info("Checking stock for product: {} with quantity: {}", productId, quantity);
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getAvailableStock() >= quantity)
                .orElse(false);
    }

    /**
     * Reserve stock for an order
     * Decreases available stock and increases reserved stock
     */
    public void reserveStock(Long productId, Integer quantity) {
        log.info("Reserving {} units for product: {}", quantity, productId);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableStock() < quantity) {
            log.warn("Insufficient stock for product: {}. Available: {}, Requested: {}",
                    productId, inventory.getAvailableStock(), quantity);
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }

        inventory.setAvailableStock(inventory.getAvailableStock() - quantity);
        inventory.setReservedStock(inventory.getReservedStock() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        inventoryRepository.save(inventory);
        log.info("Successfully reserved stock for product: {}", productId);
    }

    /**
     * Release reserved stock (when order is canceled)
     * Increases available stock and decreases reserved stock
     */
    public void releaseStock(Long productId, Integer quantity) {
        log.info("Releasing {} units for product: {}", quantity, productId);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedStock() < quantity) {
            log.warn("Cannot release more stock than reserved for product: {}", productId);
            throw new IllegalStateException("Insufficient reserved stock for product: " + productId);
        }

        inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
        inventory.setReservedStock(inventory.getReservedStock() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        inventoryRepository.save(inventory);
        log.info("Successfully released stock for product: {}", productId);
    }

    /**
     * Confirm reserved inventory (move from reserved to sold)
     * Called when payment is confirmed
     */
    public void confirmReservation(Long productId, Integer quantity) {
        log.info("Confirming reservation for product: {} with quantity: {}", productId, quantity);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedStock() < quantity) {
            log.warn("Cannot confirm more reservations than reserved for product: {}", productId);
            throw new IllegalStateException("Insufficient reserved stock for product: " + productId);
        }

        inventory.setReservedStock(inventory.getReservedStock() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        inventoryRepository.save(inventory);
        log.info("Successfully confirmed reservation for product: {}", productId);
    }

    /**
     * Update inventory (admin operation)
     * Sets available stock to specified amount
     */
    public InventoryDTO updateInventory(Long productId, Integer availableStock) {
        log.info("Updating inventory for product: {} to stock: {}", productId, availableStock);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setAvailableStock(availableStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        Inventory updated = inventoryRepository.save(inventory);
        log.info("Successfully updated inventory for product: {}", productId);
        return inventoryMapper.toDTO(updated);
    }

    /**
     * Create new inventory entry for a product
     */
    public InventoryDTO createInventory(Long productId, Integer availableStock) {
        log.info("Creating inventory for product: {} with stock: {}", productId, availableStock);
        
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            throw new IllegalStateException("Inventory already exists for product: " + productId);
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableStock(availableStock);
        inventory.setReservedStock(0);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Successfully created inventory for product: {}", productId);
        return inventoryMapper.toDTO(saved);
    }

    /**
     * Get all inventory items
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllInventory() {
        log.info("Fetching all inventory items");
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get low stock items (stock below threshold)
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockItems(Integer threshold) {
        log.info("Fetching low stock items below threshold: {}", threshold);
        return inventoryRepository.findLowStockItems(threshold).stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
