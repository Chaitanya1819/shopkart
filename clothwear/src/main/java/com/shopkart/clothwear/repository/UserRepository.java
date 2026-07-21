package com.shopkart.clothwear.repository;

import com.shopkart.clothwear.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // Optional avoids NullPointerException by safely handling missing users. because User may or may not exist, so Optional provides safer null handling.
    boolean existsByEmail(String email);
}