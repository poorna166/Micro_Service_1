package com.techcommerce.cart_service.domain.port;

import com.techcommerce.cart_service.domain.model.Cart;

import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(Long id);
    void deleteById(Long id);
}
