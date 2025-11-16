package com.techcommerce.cart_service.dto;

import jakarta.validation.constraints.NotNull;

public class RemoveCartItemRequest {
    @NotNull
    private Long itemId;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
}
