package com.shopkart.cart.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CartDto {

    @Data
    public static class AddItemRequest {
        private String userEmail;
        private Long productId;
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long cartItemId;
        private Long productId;
        private String title;
        private String brand;
        private String imageUrl;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal lineTotal; // price * quantity
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartSummaryResponse {
        private List<CartItemResponse> items;
        private BigDecimal subtotal;
        private int totalItems;
        private String currency;
    }
}