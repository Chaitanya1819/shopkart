package com.shopkart.order_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.shopkart.order_service.dto.OrderEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Wraps KafkaTemplate — called by OrderService after saving order to DB
// Publishes OrderEvent to "order.placed" topic
// Payment, Inventory, Notification services will all consume from this topic
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String TOPIC = "order.placed";

    public void publishOrderPlaced(OrderEvent event) {
        log.info("Publishing order event to topic [{}] for order: {}", TOPIC, event.getOrderNumber());

        kafkaTemplate.send(TOPIC, event.getOrderNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Order event published successfully: {} | partition: {} | offset: {}",
                                event.getOrderNumber(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish order event for order: {} | error: {}",
                                event.getOrderNumber(), ex.getMessage());
                    }
                });
    }
}