package com.shopkart.order_service.service;

import com.shopkart.order_service.client.ProductClient;
import com.shopkart.order_service.client.ProductDto;
import com.shopkart.order_service.dto.OrderDto;
import com.shopkart.order_service.dto.OrderEvent;
import com.shopkart.order_service.exception.OrderNotFoundException;
import com.shopkart.order_service.exception.ProductUnavailableException;
import com.shopkart.order_service.kafka.OrderEventPublisher;
import com.shopkart.order_service.model.Order;
import com.shopkart.order_service.model.OrderItem;
import com.shopkart.order_service.model.OrderStatus;
import com.shopkart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderDto.OrderResponse placeOrder(OrderDto.PlaceOrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Step 1 — Build OrderItems by calling Product Service for each item
        List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {

            // Call Product Service over HTTP — same pattern as Cart Service
            ProductDto product = productClient.getProductById(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductUnavailableException(itemRequest.getProductId()));

            // Get the price — use discounted price if available
            BigDecimal price = product.getDiscountedPrice() != null
                    ? product.getDiscountedPrice()
                    : product.getPrice();

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            // Snapshot product details — frozen at time of order
            return OrderItem.builder()
                    .productId(itemRequest.getProductId())
                    .productTitle(product.getTitle())
                    .brand(product.getBrand())
                    .price(price)
                    .quantity(itemRequest.getQuantity())
                    .lineTotal(lineTotal)
                    .build();

        }).collect(Collectors.toList());

        // Step 2 — Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 3 — Generate unique order number
        String orderNumber = "ORD-" + System.currentTimeMillis();

        // Step 4 — Build and save the Order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userEmail(request.getUserEmail())
                .status(OrderStatus.PLACED)
                .totalAmount(totalAmount)
                .currency("USD")
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .orderItems(orderItems)
                .build();

        // Link each OrderItem back to the Order
        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved to database: {}", savedOrder.getOrderNumber());

        // Step 5 — Publish Kafka event — Order Service is done after this
        // Payment, Inventory, Notification services react independently
        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .userEmail(savedOrder.getUserEmail())
                .totalAmount(savedOrder.getTotalAmount())
                .currency(savedOrder.getCurrency())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getCreatedAt())
                .build();

        orderEventPublisher.publishOrderPlaced(event);

        // Step 6 — Return response to client immediately
        return mapToResponse(savedOrder);
    }

    public List<OrderDto.OrderResponse> getOrdersByUser(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderDto.OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return mapToResponse(order);
    }

    @Transactional
    public OrderDto.OrderResponse updateStatus(String orderNumber, OrderDto.UpdateStatusRequest request) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        order.setStatus(request.getStatus());
        return mapToResponse(orderRepository.save(order));
    }

    // Map Order entity to OrderResponse DTO
    private OrderDto.OrderResponse mapToResponse(Order order) {
        List<OrderDto.OrderItemResponse> itemResponses = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream()
                .map(item -> OrderDto.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productTitle(item.getProductTitle())
                        .brand(item.getBrand())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userEmail(order.getUserEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .orderItems(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
