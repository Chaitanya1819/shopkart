package com.shopkart.order_service.controller;

import com.shopkart.order_service.dto.OrderDto;
import com.shopkart.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders/place — places order, saves to DB, publishes Kafka event
    @PostMapping("/place")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto.OrderResponse placeOrder(@Valid @RequestBody OrderDto.PlaceOrderRequest request) {
        return orderService.placeOrder(request);
    }

    // GET /api/orders/user/{email} — all orders for a user
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUser(@PathVariable String userEmail) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userEmail));
    }

    // GET /api/orders/{orderNumber} — single order detail
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDto.OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    // PUT /api/orders/{orderNumber}/status — update order status (admin use)
    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderDto.UpdateStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderNumber, request));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is running!");
    }

    // CORS — allows React frontend on localhost:3000 to call this service
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