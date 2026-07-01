package com.shopkart.cart.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// Mirrors the fields we need from Product Service's response.
// We don't need every field - just enough to price the cart.
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private Long id;
    private String title;
    private String brand;
    private String imageUrl;
    private BigDecimal discountedPrice;
    private BigDecimal price;
    private String currency;
}