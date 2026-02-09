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
    private int pageNumber;
    private int pageSize;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;

    private int numberOfElements;
    private boolean empty;
}
