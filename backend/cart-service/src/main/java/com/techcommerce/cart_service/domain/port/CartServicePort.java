package com.techcommerce.cart_service.domain.port;

import com.techcommerce.cart_service.domain.model.Cart;
import com.techcommerce.cart_service.domain.model.CartItem;

import java.util.Optional;

public interface CartServicePort {
    Cart createCart(Cart cart);
    Optional<Cart> getCart(Long id);
    Cart addItem(Long cartId, CartItem item);
    Cart removeItem(Long cartId, Long itemId);
}
