package com.shopkart.cart.client;

import com.shopkart.cart.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

// This class is the actual "microservice talking to another microservice" piece.
// CartService never touches the database of Product Service directly -
// it only ever goes through this HTTP client, exactly like a real frontend would.
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    private final RestClient productServiceClient;

    public Optional<ProductDto> getProductById(Long productId) {
        try {
            ProductDto product = productServiceClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductDto.class);
            return Optional.ofNullable(product);
        } catch (RestClientException e) {
            log.error("Failed to fetch product {} from Product Service: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }
}