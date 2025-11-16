package com.techcommerce.product_service.dto;

import com.techcommerce.product_service.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class ProductVariantDto {
    private Long id;
    private String variantName;
    private BigDecimal extraPrice;
}
