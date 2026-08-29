package com.shopkart.payment_service.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors the OrderEvent published by Order Service
// @JsonIgnoreProperties ensures extra fields don't cause errors
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private String userEmail;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private String createdAt;
}