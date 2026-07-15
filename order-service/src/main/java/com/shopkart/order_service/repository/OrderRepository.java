package com.shopkart.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopkart.order_service.model.Order;
import com.shopkart.order_service.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // All orders for a specific user — used in GET /api/orders/user/{email}
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // Find single order by order number — used in GET /api/orders/{orderNumber}
    Optional<Order> findByOrderNumber(String orderNumber);

    // Filter orders by status — useful for admin dashboard later
    List<Order> findByStatus(OrderStatus status);

    // Check if order number already exists — used when generating unique order numbers
    boolean existsByOrderNumber(String orderNumber);
}