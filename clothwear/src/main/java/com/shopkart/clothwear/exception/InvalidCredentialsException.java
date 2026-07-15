package com.shopkart.clothwear.exception;

// Throw this when login fails — wrong email or wrong password.
// Returns 401 Unauthorized. Deliberately vague message for security.
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}