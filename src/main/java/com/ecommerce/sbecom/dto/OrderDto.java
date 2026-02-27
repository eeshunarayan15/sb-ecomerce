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
    UUID orderId;
    String email;
    List<OrderItemDto> orderItems;
    LocalDateTime orderDate;
    PaymentDto paymentDto;
    double totalAmount;
    ;
    String OrderStatus;
    UUID addressId;

}
