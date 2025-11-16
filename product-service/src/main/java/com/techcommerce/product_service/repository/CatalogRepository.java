package com.techcommerce.product_service.repository;

import com.techcommerce.product_service.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
}
