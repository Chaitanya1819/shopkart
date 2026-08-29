package com.shopkart.order_service.controller;

import com.shopkart.order_service.dto.OrderDto;
import com.shopkart.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders/place
    // Places an order, saves it to DB, and publishes Kafka event
    @PostMapping("/place")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto.OrderResponse placeOrder(
            @Valid @RequestBody OrderDto.PlaceOrderRequest request) {

        return orderService.placeOrder(request);
    }

    // GET /api/orders/user/{email}
    // Returns all orders for a specific user
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUser(
            @PathVariable String userEmail) {

        return ResponseEntity.ok(
                orderService.getOrdersByUser(userEmail)
        );
    }

    // GET /api/orders/{orderNumber}
    // Returns a single order by order number
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDto.OrderResponse> getOrderByNumber(
            @PathVariable String orderNumber) {

        return ResponseEntity.ok(
                orderService.getOrderByNumber(orderNumber)
        );
    }

    // PUT /api/orders/{orderNumber}/status
    // Updates the status of an order
    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderDto.UpdateStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateStatus(orderNumber, request)
        );
    }

    // GET /api/orders/health
    // Simple health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {

        return ResponseEntity.ok(
                "Order Service is running!"
        );
    }
}