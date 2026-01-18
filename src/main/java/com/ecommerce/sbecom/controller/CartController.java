package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.CartDto;
import com.ecommerce.sbecom.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/carts/product/{productId}/quantity/{quantity}")
    public ResponseEntity<ApiResponse<Object>> addProductToCart(
            @PathVariable UUID productId,
            @PathVariable Integer quantity,
            Authentication authentication
    ) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                        .success(true)
                        .message("Product Added to Cart")
                        .timestamp(LocalDateTime.now().toString())
                        .data(cartDto)
                .build());
    }

}
