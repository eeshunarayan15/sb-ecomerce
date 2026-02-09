package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.OrderDto;
import com.ecommerce.sbecom.dto.OrderRequestDto;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createOrder(
            @RequestBody @Valid OrderRequestDto orderRequestDto,
            Authentication authentication

    ) {
        User user = (User) authentication.getPrincipal();
        UUID userId = user.getId();
        String email = user.getEmail();


        OrderDto orderDto = orderService.placeOrder(orderRequestDto, userId, email, orderRequestDto.getPaymentMethod());


        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .data(orderDto)
                .message("Order placed successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())

                .build());
    }

}
