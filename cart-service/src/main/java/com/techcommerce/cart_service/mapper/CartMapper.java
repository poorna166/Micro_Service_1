package com.techcommerce.cart_service.mapper;

import com.techcommerce.cart_service.domain.model.Cart;
import com.techcommerce.cart_service.domain.model.CartItem;
import com.techcommerce.cart_service.infrastructure.entity.CartEntity;
import com.techcommerce.cart_service.infrastructure.entity.CartItemEntity;

import java.util.stream.Collectors;

public final class CartMapper {
    private CartMapper() {}

    public static Cart toDomain(CartEntity e) {
        if (e == null) return null;
        Cart c = new Cart();
        c.setId(e.getId());
        c.setUserId(e.getUserId());
        c.setItems(e.getItems().stream().map(ie -> {
            CartItem i = new CartItem();
            i.setId(ie.getId());
            i.setProductId(ie.getProductId());
            i.setQuantity(ie.getQuantity());
            i.setPrice(ie.getPrice());
            return i;
        }).collect(Collectors.toList()));
        return c;
    }

    public static CartEntity toEntity(Cart c) {
        if (c == null) return null;
        CartEntity e = new CartEntity();
        e.setId(c.getId());
        e.setUserId(c.getUserId());
        if (c.getItems() != null) {
            e.setItems(c.getItems().stream().map(i -> {
                CartItemEntity ie = new CartItemEntity();
                ie.setId(i.getId());
                ie.setProductId(i.getProductId());
                ie.setQuantity(i.getQuantity());
                ie.setPrice(i.getPrice());
                ie.setCart(e);
                return ie;
            }).collect(Collectors.toList()));
        }
        return e;
    }
}
