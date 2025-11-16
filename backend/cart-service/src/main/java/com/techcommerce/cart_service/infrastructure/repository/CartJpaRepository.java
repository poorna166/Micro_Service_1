package com.techcommerce.cart_service.infrastructure.repository;

import com.techcommerce.cart_service.infrastructure.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
}
