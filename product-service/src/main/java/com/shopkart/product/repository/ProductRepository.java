package com.shopkart.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopkart.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTopLavelCategory(String topLavelCategory);

    List<Product> findByThirdLavelCategory(String thirdLavelCategory);

    List<Product> findByBrandIgnoreCase(String brand);

    List<Product> findByTitleContainingIgnoreCase(String keyword);

    long countByTopLavelCategory(String topLavelCategory);
}