package com.techcommerce.cart_service.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Long id;
    private Long userId;
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public void removeItemById(Long itemId) {
        this.items.removeIf(i -> i.getId() != null && i.getId().equals(itemId));
    }
}
