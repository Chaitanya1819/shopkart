package com.shopkart.payment_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Published to payment.success or payment.failed topic
// Notification and Analytics services consume this
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long paymentId;
    private String orderNumber;
    private String userEmail;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
    private String paymentMethod;
    private String processedAt;
}