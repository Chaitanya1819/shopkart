package com.shopkart.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private String createdAt; // String instead of LocalDateTime — avoids Jackson serialization issues
}