package com.shopkart.payment_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopkart.payment_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderNumber(String orderNumber);
    List<Payment> findByUserEmail(String userEmail);
    List<Payment> findByStatus(String status);
}