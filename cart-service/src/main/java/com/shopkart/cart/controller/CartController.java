package com.shopkart.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopkart.cart.dto.CartDto;
import com.shopkart.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDto.CartItemResponse> addItem(@RequestBody CartDto.AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<CartDto.CartSummaryResponse> getCart(@PathVariable String userEmail) {
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @DeleteMapping("/{userEmail}/item/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable String userEmail, @PathVariable Long productId) {
        cartService.removeItem(userEmail, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userEmail}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userEmail) {
        cartService.clearCart(userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Cart Service is running!");
    }
}