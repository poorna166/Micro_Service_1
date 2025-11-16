package com.techcommerce.product_service.dto;

import com.techcommerce.product_service.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {

    private Long id;
    private Product product;
    private String imageUrl;


}

