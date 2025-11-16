package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.CatalogDto;
import com.techcommerce.admin_service.dto.CategoryDto;
import com.techcommerce.admin_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    //============ PRODUCTS =============//

    @PostMapping("/api/products")
    ProductDto createProduct(@RequestBody ProductDto dto);

    @PutMapping("/api/products/{id}")
    ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto dto);

    @DeleteMapping("/api/products/{id}")
    void deleteProduct(@PathVariable Long id);

    @GetMapping("/api/products/{id}")
    ProductDto getProduct(@PathVariable Long id);


    //============ CATEGORIES =============//

    @PostMapping("/api/categories")
    CategoryDto createCategory(@RequestBody CategoryDto dto);

    @GetMapping("/api/categories/{id}")
    CategoryDto getCategory(@PathVariable Long id);


    //============ CATALOGS =============//

    @PostMapping("/api/catalogs")
    CatalogDto createCatalog(@RequestBody CatalogDto dto);

    @GetMapping("/api/catalogs/{id}")
    CatalogDto getCatalog(@PathVariable Long id);

    @GetMapping("/api/catalogs")
    List<CatalogDto> listCatalogs();
}
