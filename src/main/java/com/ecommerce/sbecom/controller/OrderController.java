package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.*;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.payload.OrderResponse;
import com.ecommerce.sbecom.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
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

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<Object>> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderRequestDto orderRequestDto
    ) {
        orderService.updateOrder(orderId, orderRequestDto);

        return ResponseEntity.ok(ApiResponse.builder()
                .message("Order updated successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/public/orders")
    public ResponseEntity<ApiResponse<Object>> getAllOrders(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        List<OrderDto> orders = orderService.getAllOrder(user.getId());

        return ResponseEntity.ok(ApiResponse.builder()
                .data(orders)
                .message("Orders fetched successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrdersAdmin(
            @RequestParam(defaultValue = "0",required = false)  int page,
            @RequestParam(defaultValue = "10",required = false) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        OrderResponse res = orderService.getAllOrderAdmin(user.getId(), page, size, sortBy, sortOrder);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Object>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody OrderStatusUpdateRequest request

    ) {

        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Order status updated successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

}
