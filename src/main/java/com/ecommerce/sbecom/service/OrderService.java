package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.OrderDto;
import com.ecommerce.sbecom.dto.OrderRequestDto;
import com.ecommerce.sbecom.model.PaymentMethod;
import jakarta.validation.Valid;

import java.util.UUID;

public interface OrderService {
    OrderDto placeOrder(@Valid OrderRequestDto orderRequestDto, UUID userId, String email, PaymentMethod paymentMethod);
}
