package com.shopkart.order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — order number doesn't exist
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(
            OrderNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // 503 — Product Service is down or product doesn't exist
    @ExceptionHandler(ProductUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleProductUnavailable(
            ProductUnavailableException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", ex.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // 400 — @Valid failed on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Validation Failed",
                "message", errors,
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // 400 — invalid argument (empty items list etc.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // 500 — anything unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
