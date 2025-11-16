package com.techcommerce.cart_service.infrastructure.repository;

import com.techcommerce.cart_service.domain.model.Cart;
import com.techcommerce.cart_service.domain.model.CartItem;
import com.techcommerce.cart_service.domain.port.CartRepositoryPort;
import com.techcommerce.cart_service.infrastructure.entity.CartEntity;
import com.techcommerce.cart_service.infrastructure.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartJpaRepository jpa;

    public CartRepositoryAdapter(CartJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cart save(Cart cart) {
        // If cart has an id, load managed entity and apply updates to preserve version and relationships.
        CartEntity entity;
        if (cart.getId() != null) {
            entity = jpa.findById(cart.getId()).orElseGet(() -> new CartEntity());
        } else {
            entity = new CartEntity();
        }

        // apply changes
        entity.setUserId(cart.getUserId());

        // replace items: clear and re-create to match domain
        entity.getItems().clear();
        if (cart.getItems() != null) {
            cart.getItems().forEach(i -> {
                CartItemEntity ie = new CartItemEntity();
                ie.setId(i.getId());
                ie.setProductId(i.getProductId());
                ie.setQuantity(i.getQuantity());
                ie.setPrice(i.getPrice());
                entity.addItem(ie);
            });
        }

        CartEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) { jpa.deleteById(id); }

    private CartEntity toEntity(Cart cart) {
        // deprecated: prefer save via managed entity path
        CartEntity e = new CartEntity();
        e.setId(cart.getId());
        e.setUserId(cart.getUserId());
        if (cart.getItems() != null) {
            e.setItems(cart.getItems().stream().map(i -> {
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

    private Cart toDomain(CartEntity e) {
        Cart c = new Cart();
        c.setId(e.getId());
        c.setUserId(e.getUserId());
        if (e.getItems() != null) {
            c.setItems(e.getItems().stream().map(ie -> {
                CartItem i = new CartItem();
                i.setId(ie.getId());
                i.setProductId(ie.getProductId());
                i.setQuantity(ie.getQuantity());
                i.setPrice(ie.getPrice());
                return i;
            }).collect(Collectors.toList()));
        }
        return c;
    }
}
