package com.techcommerce.product_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    private String name;

    private String description;

    private String brand;

    private String model;

    private BigDecimal price;

    private Integer discountPercentage;

    private Long categoryId;
    private String categoryName;

    private Integer stock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductImageDto> images;
    private List<ProductVariantDto> variants;
}

