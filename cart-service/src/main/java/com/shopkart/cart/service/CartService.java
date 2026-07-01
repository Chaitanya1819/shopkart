package com.shopkart.cart.service;

import com.shopkart.cart.client.ProductClient;
import com.shopkart.cart.dto.CartDto;
import com.shopkart.cart.dto.ProductDto;
import com.shopkart.cart.model.CartItem;
import com.shopkart.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    public CartDto.CartItemResponse addItem(CartDto.AddItemRequest request) {
        // 1. Ask Product Service if this product actually exists, and what it costs right now.
        ProductDto product = productClient.getProductById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product " + request.getProductId() + " not found in Product Service"));

        // 2. If this user already has this product in their cart, just bump the quantity.
        Optional<CartItem> existing = cartItemRepository
                .findByUserEmailAndProductId(request.getUserEmail(), request.getProductId());

        CartItem savedItem;
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            savedItem = cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .userEmail(request.getUserEmail())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            savedItem = cartItemRepository.save(newItem);
        }

        return toResponse(savedItem, product);
    }

    public CartDto.CartSummaryResponse getCart(String userEmail) {
        List<CartItem> cartItems = cartItemRepository.findByUserEmail(userEmail);

        List<CartDto.CartItemResponse> itemResponses = cartItems.stream()
                .map(item -> {
                    // Live lookup every time - this is what keeps cart prices always in sync
                    // with whatever the admin currently has set in Product Service.
                    ProductDto product = productClient.getProductById(item.getProductId())
                            .orElse(null);
                    if (product == null) return null; // product may have been deleted
                    return toResponse(item, product);
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemResponses.stream()
                .map(CartDto.CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemResponses.stream()
                .mapToInt(CartDto.CartItemResponse::getQuantity)
                .sum();

        return CartDto.CartSummaryResponse.builder()
                .items(itemResponses)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .currency("USD")
                .build();
    }

    public void removeItem(String userEmail, Long productId) {
        cartItemRepository.deleteByUserEmailAndProductId(userEmail, productId);
    }

    public void clearCart(String userEmail) {
        cartItemRepository.deleteByUserEmail(userEmail);
    }

    private CartDto.CartItemResponse toResponse(CartItem item, ProductDto product) {
        BigDecimal price = product.getDiscountedPrice() != null
                ? product.getDiscountedPrice()
                : product.getPrice();

        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartDto.CartItemResponse.builder()
                .cartItemId(item.getId())
                .productId(item.getProductId())
                .title(product.getTitle())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .price(price)
                .quantity(item.getQuantity())
                .lineTotal(lineTotal)
                .currency(product.getCurrency())
                .build();
    }
}