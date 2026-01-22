package com.ecommerce.sbecom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private String productId;
    private String productName;
    private String description;
    private Integer quantity;
    private Double price;
    private double specialPrice;
    private CategoryDto categoryDto;
}
