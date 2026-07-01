package com.shopkart.product.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductJsonDto {
    private String imageUrl;
    private String brand;
    private String title;
    private String color;
    private BigDecimal discountedPrice;
    private BigDecimal price;
    private Integer discountPersent;
    private List<SizeDto> size;
    private Integer quantity;
    private String topLavelCategory;
    private String secondLavelCategory;
    private String thirdLavelCategory;
    private String description;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SizeDto {
        private String name;
        private Integer quantity;
    }
}