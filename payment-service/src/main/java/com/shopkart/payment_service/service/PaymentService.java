package com.shopkart.payment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.shopkart.payment_service.dto.OrderEvent;
import com.shopkart.payment_service.model.Payment;
import com.shopkart.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Called by PaymentEventListener when order.placed event arrives
    public Payment processPayment(OrderEvent orderEvent) {
        log.info("Processing payment for order: {}", orderEvent.getOrderNumber());

        // Simulate payment — 90% success rate
        boolean paymentSuccess = new Random().nextInt(10) != 0;
        String status = paymentSuccess ? "SUCCESS" : "FAILED";

        // Build and save payment record
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