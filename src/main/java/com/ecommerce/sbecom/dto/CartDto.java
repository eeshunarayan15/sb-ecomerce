package com.ecommerce.sbecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private UUID cartId;
    private Double totalPrice=0.0;
    private Integer quantity;

    List<ProductDto> productDtoList = new ArrayList<>();
}
