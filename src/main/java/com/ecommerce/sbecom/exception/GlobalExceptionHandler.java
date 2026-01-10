package com.ecommerce.sbecom.exception;

import com.ecommerce.sbecom.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(EntityNotFoundException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(e.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateEntry(DataIntegrityViolationException e) {
        String message = "Data already exists";
        
        // Check if it's category name duplicate
        if (e.getMessage().contains("category_name") || e.getMessage().contains("UKlroeo5fvfdeg4hpicn4lw7x9b")) {
            message = "Category name already exists";
        }
        
        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .message(message)  // ✅ User-friendly message
            .data(null)
            .timestamp(LocalDateTime.now().toString())
            .build();
            
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // 409 Conflict
    }
       // Handle missing request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingRequestBody(HttpMessageNotReadableException e) {
        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .message("Request body is required")  // ✅ User-friendly message
            .data(null)
            .timestamp(LocalDateTime.now().toString())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .message("Internal server error: " + e.getMessage())
            .data(null)
            .timestamp(LocalDateTime.now().toString())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    

}
