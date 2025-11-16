package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.CategoryDto;
import com.techcommerce.admin_service.feignclients.ProductClient;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoryService {

    private final ProductClient productClient;

    public AdminCategoryService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public Object createCategory(CategoryDto dto) {
        return productClient.createCategory(dto);
    }

    public Object getCategory(Long id) {
        return productClient.getCategory(id);
    }
}
