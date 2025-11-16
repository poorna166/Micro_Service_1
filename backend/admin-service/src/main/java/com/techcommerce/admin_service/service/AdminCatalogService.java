package com.techcommerce.admin_service.service;


import com.techcommerce.admin_service.dto.CatalogDto;
import com.techcommerce.admin_service.feignclients.ProductClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCatalogService {

    private final ProductClient productClient;

    public AdminCatalogService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public CatalogDto createCatalog(CatalogDto dto) {
        return productClient.createCatalog(dto);
    }

    public CatalogDto getCatalog(Long id) {
        return productClient.getCatalog(id);
    }

    public List<CatalogDto> listCatalogs() {
        return productClient.listCatalogs();
    }
}
