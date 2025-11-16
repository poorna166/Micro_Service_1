package com.techcommerce.product_service.controller;

import com.techcommerce.product_service.dto.CategoryDto;
import com.techcommerce.product_service.dto.CatalogDto;
import com.techcommerce.product_service.dto.ProductDto;
import com.techcommerce.product_service.mapper.DtoEntityMapper;
import com.techcommerce.product_service.model.Category;
import com.techcommerce.product_service.model.Catalog;
import com.techcommerce.product_service.model.Product;
import com.techcommerce.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Categories
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        Category c = DtoEntityMapper.mapNewCategory(dto);
        Category created = productService.createCategory(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoEntityMapper.mapCategoryToDto(created));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return productService.getCategory(id).map(c -> ResponseEntity.ok(DtoEntityMapper.mapCategoryToDto(c))).orElse(ResponseEntity.notFound().build());
    }

    // Catalogs
    @PostMapping("/catalogs")
    public ResponseEntity<CatalogDto> createCatalog(@RequestBody CatalogDto dto) {
        Catalog c = DtoEntityMapper.mapNewCatalog(dto);
        Catalog created = productService.createCatalog(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoEntityMapper.mapCatalogToDto(created));
    }

    @GetMapping("/catalogs/{id}")
    public ResponseEntity<CatalogDto> getCatalog(@PathVariable Long id) {
        return productService.getCatalog(id).map(c -> ResponseEntity.ok(DtoEntityMapper.mapCatalogToDto(c))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogDto>> listCatalogs() {
        return ResponseEntity.ok(productService.listCatalogs().stream()
                .map(DtoEntityMapper::mapCatalogToDto)
                .toList());
    }

    // Products
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        ProductDto p = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.getProduct(id).map(p -> ResponseEntity.ok(DtoEntityMapper.mapProductToDto(p))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}/products")
    public Page<ProductDto> listByCategory(@PathVariable Long categoryId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return productService.listByCategory(categoryId, page, size).map(DtoEntityMapper::mapProductToDto);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        try {
            Product p = productService.updateProduct(id, dto);
            return ResponseEntity.ok(DtoEntityMapper.mapProductToDto(p));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
