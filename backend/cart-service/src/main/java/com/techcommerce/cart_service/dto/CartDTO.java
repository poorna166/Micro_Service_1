package com.techcommerce.cart_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }

    public static class CartItemDTO {
        private Long id;
        private Long productId;
        private Integer quantity;
        private BigDecimal price;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
