package com.ecommerce.sbecom.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private UUID orderId;
    private String email;
    List<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private PaymentDto paymentDto;
    private double totalAmount;;
    private String OrderStatus;
    private UUID addressId;

}
