package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CartDto;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface   CartService {

    CartDto addProductToCart(UUID productId, Integer quantity, Authentication authentication);
}
