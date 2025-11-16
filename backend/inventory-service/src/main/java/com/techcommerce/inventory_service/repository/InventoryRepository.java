package com.techcommerce.inventory_service.repository;

import com.techcommerce.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Find inventory by product ID
     */
    Optional<Inventory> findByProductId(Long productId);
    
    /**
     * Find all items with available stock below a threshold
     */
    @Query("SELECT i FROM Inventory i WHERE i.availableStock < :threshold")
    List<Inventory> findLowStockItems(@Param("threshold") Integer threshold);
}