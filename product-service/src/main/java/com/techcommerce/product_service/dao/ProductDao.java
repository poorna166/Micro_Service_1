package com.techcommerce.product_service.dao;

import com.techcommerce.product_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductDao {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
