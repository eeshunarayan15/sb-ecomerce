package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.controller.OrderStatusUpdateRequest;
import com.ecommerce.sbecom.dto.OrderDto;
import com.ecommerce.sbecom.dto.OrderRequestDto;
import com.ecommerce.sbecom.model.PaymentMethod;
import com.ecommerce.sbecom.payload.OrderResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto placeOrder(@Valid OrderRequestDto orderRequestDto, UUID userId, String email, PaymentMethod paymentMethod);

    void updateOrder(UUID orderId, OrderRequestDto orderRequestDto);
    List<OrderDto> getAllOrder(UUID userId);

    OrderResponse getAllOrderAdmin(UUID id, int page, int size, String sortBy, String sortOrder);


    void updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request);
}
