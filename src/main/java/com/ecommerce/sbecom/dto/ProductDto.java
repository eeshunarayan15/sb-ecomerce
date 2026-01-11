package com.ecommerce.sbecom.dto;

import com.ecommerce.sbecom.entiry.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
