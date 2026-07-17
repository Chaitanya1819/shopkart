package com.shopkart.order.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

// Same pattern as Cart Service ProductClient
// Calls Product Service over HTTP to fetch product details at time of order
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    @Value("${product.service.url}")
    private String productServiceUrl;

    public Optional<ProductDto> getProductById(Long productId) {
        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(productServiceUrl)
                    .build();

            ProductDto product = restClient.get()
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
