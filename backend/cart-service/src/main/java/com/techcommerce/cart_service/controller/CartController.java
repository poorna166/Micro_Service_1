package com.techcommerce.cart_service.controller;

import com.techcommerce.cart_service.domain.model.Cart;
import com.techcommerce.cart_service.domain.model.CartItem;
import com.techcommerce.cart_service.domain.port.CartServicePort;
import com.techcommerce.cart_service.dto.AddCartItemRequest;
import com.techcommerce.cart_service.dto.CartDTO;
import com.techcommerce.cart_service.dto.RemoveCartItemRequest;
import com.techcommerce.cart_service.exception.CartNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/carts")
@Validated
@Tag(name = "cart", description = "Operations for shopping cart management")
public class CartController {

    private final CartServicePort cartService;

    public CartController(CartServicePort cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Create a new cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart created", content = @Content(schema = @Schema(implementation = Cart.class)))
    })
    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody Cart c) {
        Cart created = cartService.createCart(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get cart by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart found", content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        Optional<Cart> c = cartService.getCart(id);
        return c.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add item to cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added", content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PostMapping("/{id}/items")
    public ResponseEntity<Cart> addItem(@PathVariable Long id, @Valid @RequestBody AddCartItemRequest req) {
        try {
            CartItem item = new CartItem();
            item.setProductId(req.getProductId());
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());
            Cart updated = cartService.addItem(id, item);
            return ResponseEntity.ok(updated);
        } catch (CartNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Remove item from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed", content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            Cart updated = cartService.removeItem(id, itemId);
            return ResponseEntity.ok(updated);
        } catch (CartNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
