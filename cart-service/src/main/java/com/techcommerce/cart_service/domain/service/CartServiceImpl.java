package com.techcommerce.cart_service.domain.service;

import com.techcommerce.cart_service.domain.model.Cart;
import com.techcommerce.cart_service.domain.model.CartItem;
import com.techcommerce.cart_service.domain.port.CartRepositoryPort;
import com.techcommerce.cart_service.domain.port.CartServicePort;
import com.techcommerce.cart_service.exception.CartNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartServicePort {

    private final CartRepositoryPort repository;

    public CartServiceImpl(CartRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Cart createCart(Cart cart) {
        return repository.save(cart);
    }

    @Override
    public Optional<Cart> getCart(Long id) {
        return repository.findById(id);
    }

    @Override
    public Cart addItem(Long cartId, CartItem item) {
        Cart cart = repository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        cart.addItem(item);
        return repository.save(cart);
    }

    @Override
    public Cart removeItem(Long cartId, Long itemId) {
        Cart cart = repository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        cart.removeItemById(itemId);
        return repository.save(cart);
    }
}
