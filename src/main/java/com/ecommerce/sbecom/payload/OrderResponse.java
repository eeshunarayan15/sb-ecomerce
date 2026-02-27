package com.ecommerce.sbecom.payload;
import com.ecommerce.sbecom.dto.OrderDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private List<OrderDto> orderDto;
    private Integer page;
    private Integer size;
    private long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
