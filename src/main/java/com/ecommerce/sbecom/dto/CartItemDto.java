package com.ecommerce.sbecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private UUID cartId;
    private UUID cartItemId;
    private CartDto cartDto;
    private ProductDto productDto;
    private Integer discount;
    private Integer quantity;

    private Double productPrice = 0.0;
}
