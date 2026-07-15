package com.shopkart.clothwear.exception;

// Throw this when someone tries to register with an email that already exists.
// GlobalExceptionHandler catches it and returns 409 Conflict automatically.
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email is already registered: " + email);
    }
}