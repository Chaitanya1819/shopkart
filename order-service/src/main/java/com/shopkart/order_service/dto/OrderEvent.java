package com.shopkart.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// This is what gets published to Kafka topic "order.placed"
// Payment, Inventory, Notification services will receive this exact object
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private String userEmail;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}