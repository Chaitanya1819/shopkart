package com.shopkart.order.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long productId) {
        super("Product not available with id: " + productId + ". Product Service may be down or product does not exist.");
    }
}
