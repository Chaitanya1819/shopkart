package com.shopkart.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.shopkart.order_service.model.OrderStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {

    @Data
    public static class PlaceOrderRequest {

        @NotBlank(message = "User email is required")
        @Email(message = "Must be a valid email")
        private String userEmail;

        @NotBlank(message = "Shipping address is required")
        private String shippingAddress;

        @NotBlank(message = "Shipping city is required")
        private String shippingCity;

        private String shippingState;

        @NotNull(message = "Order items cannot be null")
        private List<OrderItemRequest> items;
    }

    @Data
    public static class OrderItemRequest {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private String userEmail;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private String currency;
        private String shippingAddress;
        private String shippingCity;
        private String shippingState;
        private List<OrderItemResponse> orderItems;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productTitle;
        private String brand;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal lineTotal;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotNull(message = "Status is required")
        private OrderStatus status;
    }
}