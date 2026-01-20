package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CartDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface   CartService {

    CartDto addProductToCart(UUID productId, Integer quantity, Authentication authentication);

    List<CartDto> getAllCarts();

    CartDto getCart(UUID id, String email);

    CartDto updateProudctQuantityInCart(UUID userId, UUID productId, String action);

    CartDto deleteProductFromCart(UUID userId, UUID productId);
}
