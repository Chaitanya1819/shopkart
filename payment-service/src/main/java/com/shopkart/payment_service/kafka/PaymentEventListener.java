package com.shopkart.payment_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.shopkart.payment_service.dto.OrderEvent;
import com.shopkart.payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// This is the KEY file — @KafkaListener automatically fires
// whenever Order Service publishes to order.placed topic.
// No manual polling needed — Spring handles everything.
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order.placed", groupId = "payment-group")
    public void handleOrderPlaced(OrderEvent orderEvent) {
        log.info("Received order.placed event for order: {} | amount: {} {}",
                orderEvent.getOrderNumber(),
                orderEvent.getTotalAmount(),
                orderEvent.getCurrency());

        paymentService.processPayment(orderEvent);
    }
}