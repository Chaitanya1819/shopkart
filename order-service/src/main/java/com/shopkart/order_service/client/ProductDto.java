package com.shopkart.order.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

// Mirrors the JSON shape returned by Product Service
// Only fields Order Service needs to build the order snapshot
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
