package com.techcommerce.admin_service.feignclients;

import com.techcommerce.admin_service.dto.CatalogDto;
import com.techcommerce.admin_service.dto.CategoryDto;
import com.techcommerce.admin_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    //============ PRODUCTS =============//

    @PostMapping("/api/products")
    @CircuitBreaker(name = "productService", fallbackMethod = "createProductFallback")
    @Retry(name = "productService")
    ProductDto createProduct(@RequestBody ProductDto dto);

    default ProductDto createProductFallback(ProductDto dto, Throwable t) {
        // Return a minimal DTO indicating failure
        ProductDto fallback = new ProductDto();
        fallback.setName("Unavailable - create failed");
        return fallback;
    }

    @PutMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "updateProductFallback")
    @Retry(name = "productService")
    ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto dto);

    default ProductDto updateProductFallback(Long id, ProductDto dto, Throwable t) {
        ProductDto fallback = new ProductDto();
        fallback.setId(id);
        fallback.setName("Unavailable - update failed");
        return fallback;
    }

    @DeleteMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "deleteProductFallback")
    @Retry(name = "productService")
    void deleteProduct(@PathVariable Long id);

    default void deleteProductFallback(Long id, Throwable t) {
        // noop fallback - log at runtime
    }

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    ProductDto getProduct(@PathVariable Long id);

    default ProductDto getProductFallback(Long id, Throwable t) {
        ProductDto fallback = new ProductDto();
        fallback.setId(id);
        fallback.setName("Service Unavailable");
        fallback.setPrice(java.math.BigDecimal.ZERO);
        fallback.setStock(0);
        return fallback;
    }


    //============ CATEGORIES =============//

    @PostMapping("/api/categories")
    @CircuitBreaker(name = "productService", fallbackMethod = "createCategoryFallback")
    @Retry(name = "productService")
    CategoryDto createCategory(@RequestBody CategoryDto dto);

    default CategoryDto createCategoryFallback(CategoryDto dto, Throwable t) {
        CategoryDto fallback = new CategoryDto();
        fallback.setName("Unavailable - create failed");
        return fallback;
    }

    @GetMapping("/api/categories/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getCategoryFallback")
    @Retry(name = "productService")
    CategoryDto getCategory(@PathVariable Long id);

    default CategoryDto getCategoryFallback(Long id, Throwable t) {
        CategoryDto fallback = new CategoryDto();
        fallback.setId(id);
        fallback.setName("Unavailable");
        return fallback;
    }


    //============ CATALOGS =============//

    @PostMapping("/api/catalogs")
    @CircuitBreaker(name = "productService", fallbackMethod = "createCatalogFallback")
    @Retry(name = "productService")
    CatalogDto createCatalog(@RequestBody CatalogDto dto);

    default CatalogDto createCatalogFallback(CatalogDto dto, Throwable t) {
        CatalogDto fallback = new CatalogDto();
        fallback.setName("Unavailable - create failed");
        return fallback;
    }

    @GetMapping("/api/catalogs/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getCatalogFallback")
    @Retry(name = "productService")
    CatalogDto getCatalog(@PathVariable Long id);

    default CatalogDto getCatalogFallback(Long id, Throwable t) {
        CatalogDto fallback = new CatalogDto();
        fallback.setId(id);
        fallback.setName("Unavailable");
        return fallback;
    }

    @GetMapping("/api/catalogs")
    @CircuitBreaker(name = "productService", fallbackMethod = "listCatalogsFallback")
    @Retry(name = "productService")
    List<CatalogDto> listCatalogs();

    default List<CatalogDto> listCatalogsFallback(Throwable t) {
        return java.util.Collections.emptyList();
    }
}
