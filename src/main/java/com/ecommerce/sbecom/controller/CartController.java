package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.CartDto;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping("/carts")
    public ResponseEntity<ApiResponse<Object>> getAllCarts() {
        List<CartDto> allCarts = cartService.getAllCarts();
        return ResponseEntity.ok().body(ApiResponse.builder()
                .success(true)
                .message("All Carts")
                .data(allCarts)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }


    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<Object>> getCartById(
            @AuthenticationPrincipal User user  // ✅ BEST
    ) {
        UUID id = user.getId();
        String email = user.getEmail();
        CartDto cart = cartService.getCart(id, email);

        return ResponseEntity.ok().body(ApiResponse.builder()
                .message("Cart")
                .success(true)
                .data(cart)
                .timestamp(LocalDateTime.now().toString())
                .build());

    }

    @PatchMapping("/cart/items/{productId}")
    public ResponseEntity<ApiResponse<Object>> updateCartProduct(
            @PathVariable UUID productId,
            @RequestParam String action,
            @AuthenticationPrincipal User user
    ) {
        UUID userId = user.getId();
        CartDto cartDto = cartService.updateProudctQuantityInCart(userId, productId, action);
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .success(true)
                        .message("Updated the quantity")
                        .timestamp(LocalDateTime.now().toString())
                        .data(cartDto)
                .build());
    }
    @DeleteMapping("/cart/items/{productId}")
    public ResponseEntity<ApiResponse<Object>> deleteProductFromCart(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user
    ) {
        UUID userId = user.getId();
        CartDto cartDto = cartService.deleteProductFromCart(userId, productId);
        return ResponseEntity.ok().body(ApiResponse.builder()
                .success(true)
                .message("Deleted the product from cart")
                .timestamp(LocalDateTime.now().toString())
                .data(cartDto)
                .build());
    }

}
