package com.shopkart.payment_service.service;

import com.shopkart.payment_service.dto.OrderEvent;
import com.shopkart.payment_service.dto.PaymentEvent;
import com.shopkart.payment_service.model.Payment;
import com.shopkart.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String SUCCESS_TOPIC = "payment.success";
    private static final String FAILED_TOPIC = "payment.failed";

    public Payment processPayment(OrderEvent orderEvent) {
        log.info("Processing payment for order: {}", orderEvent.getOrderNumber());

        // Simulate payment — 90% success rate
        boolean paymentSuccess = new Random().nextInt(10) != 0;
        String status = paymentSuccess ? "SUCCESS" : "FAILED";

        // Save payment record to database
        Payment payment = Payment.builder()
                .orderId(orderEvent.getOrderId())
                .orderNumber(orderEvent.getOrderNumber())
                .userEmail(orderEvent.getUserEmail())
                .amount(orderEvent.getTotalAmount())
                .status(status)
                .paymentMethod("CARD")
                .processedAt(LocalDateTime.now().toString())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment saved — id: {} | order: {} | status: {}",
                saved.getId(), saved.getOrderNumber(), saved.getStatus());

        // Build Kafka event
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .paymentId(saved.getId())
                .orderNumber(saved.getOrderNumber())
                .userEmail(saved.getUserEmail())
                .amount(saved.getAmount())
                .status(status)
                .paymentMethod(saved.getPaymentMethod())
                .processedAt(saved.getProcessedAt())
                .build();

        // Publish to correct topic based on result
        String topic = paymentSuccess ? SUCCESS_TOPIC : FAILED_TOPIC;
        kafkaTemplate.send(topic, orderEvent.getOrderNumber(), paymentEvent);
        log.info("PaymentEvent published to topic [{}] for order: {}", topic, orderEvent.getOrderNumber());

        return saved;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByUser(String userEmail) {
        return paymentRepository.findByUserEmail(userEmail);
    }

    public Optional<Payment> getPaymentByOrderNumber(String orderNumber) {
        return paymentRepository.findByOrderNumber(orderNumber);
    }

    public long getTotalPayments() {
        return paymentRepository.count();
    }
}
