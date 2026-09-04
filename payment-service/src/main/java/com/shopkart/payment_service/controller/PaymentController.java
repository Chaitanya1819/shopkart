package com.shopkart.payment_service.controller;

import com.shopkart.payment_service.dto.OrderEvent;
import com.shopkart.payment_service.model.Payment;
import com.shopkart.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Get payments by user
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable String userEmail) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userEmail));
    }

    // Get payment by order number
    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable String orderNumber) {
        return paymentService.getPaymentByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Manual trigger for testing without Kafka
    @PostMapping("/process")
    public ResponseEntity<Payment> processManually(@RequestBody OrderEvent orderEvent) {
        return ResponseEntity.ok(paymentService.processPayment(orderEvent));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        long count = paymentService.getTotalPayments();
        return ResponseEntity.ok("Payment Service is running! Total payments: " + count);
    }

    // CORS for React frontend
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}
