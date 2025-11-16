package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.ProductDto;
import com.techcommerce.admin_service.feignclients.ProductClient;
import org.springframework.stereotype.Service;

@Service
public class AdminProductService {

    private final ProductClient productClient;

    public AdminProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    /** Create a new product */
    public ProductDto createProduct(ProductDto dto) {
        return productClient.createProduct(dto);
    }

    /** Update existing product */
    public ProductDto updateProduct(Long id, ProductDto dto) {
        return productClient.updateProduct(id, dto);
    }

    /** Get product by ID */
    public ProductDto getProduct(Long id) {
        return productClient.getProduct(id);
    }

    /** Delete product */
    public void deleteProduct(Long id) {
        productClient.deleteProduct(id);
    }
}
