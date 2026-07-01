package com.shopkart.product.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String imageUrl;

    private String brand;

    @Column(length = 500)
    private String title;

    private String color;

    // Always BigDecimal for money - never double/float
    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer discountPersent;

    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    private List<Size> size;

    private Integer quantity;

    private String topLavelCategory;
    private String secondLavelCategory;
    private String thirdLavelCategory;

    @Column(length = 2000)
    private String description;

    @Builder.Default
    private String currency = "USD";
}