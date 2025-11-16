package com.techcommerce.product_service.mapper;

import com.techcommerce.product_service.dto.*;
import com.techcommerce.product_service.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DtoEntityMapper {

    private DtoEntityMapper() {}

    // Category
    public static Category mapNewCategory(CategoryDto dto) {
        if (dto == null) return null;
        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return c;
    }

    public static void applyUpdateToCategory(Category existing, CategoryDto dto) {
        if (existing == null || dto == null) return;
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
    }

    // Product
    public static Product mapNewProduct(ProductDto dto, Category category) {
        if (dto == null) return null;
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setBrand(dto.getBrand());
        p.setModel(dto.getModel());
        p.setPrice(dto.getPrice());
        p.setDiscountPercentage(dto.getDiscountPercentage());
        p.setStock(dto.getStock());
        p.setCategory(category);
        if (dto.getImages() != null) {
            List<ProductImage> imgs = dto.getImages().stream()
                    .map(DtoEntityMapper::mapNewProductImage)
                    .collect(Collectors.toList());
            imgs.forEach(i -> i.setProduct(p));
            p.setImages(imgs);
        }
        if (dto.getVariants() != null) {
            List<ProductVariant> vars = dto.getVariants().stream()
                    .map(DtoEntityMapper::mapNewProductVariant)
                    .collect(Collectors.toList());
            vars.forEach(v -> v.setProduct(p));
            p.setVariants(vars);
        }
        return p;
    }
    //productdto
    public static ProductDto mapToProductDto(Product p) {
        if (p == null) return null;

        ProductDto dto = new ProductDto();

        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setBrand(p.getBrand());
        dto.setModel(p.getModel());
        dto.setPrice(p.getPrice());
        dto.setDiscountPercentage(p.getDiscountPercentage());
        dto.setStock(p.getStock());

        // Category
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }

        // Images
        if (p.getImages() != null) {
            List<ProductImageDto> imgs = p.getImages().stream()
                    .map(DtoEntityMapper::mapToProductImageDto)
                    .collect(Collectors.toList());
            dto.setImages(imgs);
        }

        // Variants
        if (p.getVariants() != null) {
            List<ProductVariantDto> vars = p.getVariants().stream()
                    .map(DtoEntityMapper::mapToProductVariantDto)
                    .collect(Collectors.toList());
            dto.setVariants(vars);
        }

        return dto;
    }


    public static void applyUpdateToProduct(Product existing, ProductDto dto, Category category) {
        if (existing == null || dto == null) return;
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getBrand() != null) existing.setBrand(dto.getBrand());
        if (dto.getModel() != null) existing.setModel(dto.getModel());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getDiscountPercentage() != null) existing.setDiscountPercentage(dto.getDiscountPercentage());
        if (dto.getStock() != null) existing.setStock(dto.getStock());
        if (category != null) existing.setCategory(category);

        if (dto.getImages() != null) {
            List<ProductImage> imgs = new ArrayList<>();
            for (ProductImageDto idto : dto.getImages()) {
                ProductImage i = (idto.getId() == null) ? mapNewProductImage(idto) : mapExistingProductImage(idto);
                i.setProduct(existing);
                imgs.add(i);
            }
            existing.getImages().clear();
            existing.getImages().addAll(imgs);
        }

        if (dto.getVariants() != null) {
            List<ProductVariant> vars = new ArrayList<>();
            for (ProductVariantDto vdto : dto.getVariants()) {
                ProductVariant v = (vdto.getId() == null) ? mapNewProductVariant(vdto) : mapExistingProductVariant(vdto);
                v.setProduct(existing);
                vars.add(v);
            }
            existing.getVariants().clear();
            existing.getVariants().addAll(vars);
        }
    }

    // Catalog
    public static Catalog mapNewCatalog(CatalogDto dto) {
        if (dto == null) return null;
        Catalog c = new Catalog();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return c;
    }

    public static void applyUpdateToCatalog(Catalog existing, CatalogDto dto) {
        if (existing == null || dto == null) return;
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
    }

    // ProductImage
    public static ProductImage mapNewProductImage(ProductImageDto dto) {
        if (dto == null) return null;
        ProductImage i = new ProductImage();
        i.setImageUrl(dto.getImageUrl());
        return i;
    }

    public static ProductImage mapExistingProductImage(ProductImageDto dto) {
        ProductImage i = mapNewProductImage(dto);
        i.setId(dto.getId());
        return i;
    }

    // ProductVariant
    public static ProductVariant mapNewProductVariant(ProductVariantDto dto) {
        if (dto == null) return null;
        ProductVariant v = new ProductVariant();
        v.setVariantName(dto.getVariantName());
        v.setExtraPrice(dto.getExtraPrice());
        return v;
    }

    public static ProductVariant mapExistingProductVariant(ProductVariantDto dto) {
        ProductVariant v = mapNewProductVariant(dto);
        v.setId(dto.getId());
        return v;
    }


    public static ProductImageDto mapToProductImageDto(ProductImage img) {
        if (img == null) return null;

        return new ProductImageDto(
                img.getId(),
                img.getProduct(),
                img.getImageUrl()

        );
    }

    public static ProductVariantDto mapToProductVariantDto(ProductVariant v) {
        if (v == null) return null;

        return new ProductVariantDto(
                v.getId(),
                v.getVariantName(),
                v.getExtraPrice()
        );
    }

    // Category DTO mapping
    public static CategoryDto mapCategoryToDto(Category category) {
        if (category == null) return null;
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    // Catalog DTO mapping
    public static CatalogDto mapCatalogToDto(Catalog catalog) {
        if (catalog == null) return null;
        CatalogDto dto = new CatalogDto();
        dto.setId(catalog.getId());
        dto.setName(catalog.getName());
        dto.setDescription(catalog.getDescription());
        return dto;
    }

    // Product DTO mapping (alias for mapToProductDto for consistency)
    public static ProductDto mapProductToDto(Product product) {
        return mapToProductDto(product);
    }

}
