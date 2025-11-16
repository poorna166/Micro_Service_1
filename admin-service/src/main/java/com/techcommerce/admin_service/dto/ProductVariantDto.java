package com.techcommerce.admin_service.dto;

import java.math.BigDecimal;

public class ProductVariantDto {
    private Long id;
    private String variantName;
    private BigDecimal extraPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public BigDecimal getExtraPrice() { return extraPrice; }
    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }
}
