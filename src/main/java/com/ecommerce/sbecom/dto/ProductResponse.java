package com.ecommerce.sbecom.dto;



import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private List<ProductDto> content;
}
