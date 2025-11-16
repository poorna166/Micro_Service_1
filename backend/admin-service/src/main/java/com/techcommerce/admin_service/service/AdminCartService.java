package com.techcommerce.admin_service.service;

import com.techcommerce.admin_service.dto.CartDto;
import com.techcommerce.admin_service.feignclients.CartClient;
import org.springframework.stereotype.Service;

@Service
public class AdminCartService {

    private final CartClient cartClient;

    public AdminCartService(CartClient cartClient) {
        this.cartClient = cartClient;
    }

    public CartDto createCart(CartDto dto) {
        return cartClient.createCart(dto);
    }

    public CartDto getCart(Long id) {
        return cartClient.getCart(id);
    }

    public CartDto updateCart(Long id, CartDto dto) {
        return cartClient.updateCart(id, dto);
    }

    public void deleteCart(Long id) {
        cartClient.deleteCart(id);
    }
}
