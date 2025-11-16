package com.techcommerce.product_service.service;

import com.techcommerce.product_service.dao.ProductDao;
import com.techcommerce.product_service.dto.ProductDto;
import com.techcommerce.product_service.mapper.DtoEntityMapper;
import com.techcommerce.product_service.model.Category;
import com.techcommerce.product_service.model.Catalog;
import com.techcommerce.product_service.model.Product;
import com.techcommerce.product_service.repository.CategoryRepository;
import com.techcommerce.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final com.techcommerce.product_service.repository.CatalogRepository catalogRepository;
    private final ProductDao productDao;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          com.techcommerce.product_service.repository.CatalogRepository catalogRepository,
                          ProductDao productDao) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.catalogRepository = catalogRepository;
        this.productDao = productDao;
    }

    // Catalog operations
    public Catalog createCatalog(Catalog catalog) { return catalogRepository.save(catalog); }

    public Optional<Catalog> getCatalog(Long id) { return catalogRepository.findById(id); }

    public java.util.List<Catalog> listCatalogs() { return catalogRepository.findAll(); }

    public Catalog updateCatalog(Long id, Catalog patch) {
        return catalogRepository.findById(id).map(existing -> {
            if (patch.getName() != null) existing.setName(patch.getName());
            if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
            return catalogRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Catalog not found"));
    }

    public void deleteCatalog(Long id) { catalogRepository.deleteById(id); }

    public Category createCategory(Category category) { return categoryRepository.save(category); }

    public Optional<Category> getCategory(Long id) { return categoryRepository.findById(id); }

    public ProductDto createProduct(ProductDto dto) {
        Category c = null;
        if (dto.getCategoryId() != null) c = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        Product p = DtoEntityMapper.mapNewProduct(dto, c);
        return DtoEntityMapper.mapToProductDto(productRepository.save(p));
    }

    public Optional<Product> getProduct(Long id) { return productRepository.findById(id); }

    public Page<Product> listByCategory(Long categoryId, int page, int size) {
        return productDao.findByCategoryId(categoryId, PageRequest.of(page, size));
    }

    public Product updateProduct(Long id, ProductDto dto) {
        return productRepository.findById(id).map(existing -> {
            Category c = null;
            if (dto.getCategoryId() != null) c = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            DtoEntityMapper.applyUpdateToProduct(existing, dto, c);
            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) { productRepository.deleteById(id); }
}
