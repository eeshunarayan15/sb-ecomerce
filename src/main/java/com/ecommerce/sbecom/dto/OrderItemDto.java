package com.ecommerce.sbecom.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID orderItemId;
    private ProductDto productDto;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;

}
