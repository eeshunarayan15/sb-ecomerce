package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.OrderDto;
import com.ecommerce.sbecom.dto.OrderRequestDto;
import com.ecommerce.sbecom.model.PaymentMethod;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PutMapping("/users/payments/{paymentMethod}")
    public void createOrder(
            @RequestBody  @Valid  OrderRequestDto orderRequestDto,
            Authentication authentication,
           @RequestParam PaymentMethod paymentMethod
    ){
   User user=(User) authentication.getPrincipal();
        UUID userId = user.getId();
        String email = user.getEmail();


      OrderDto orderDto= orderService.placeOrder(orderRequestDto,userId,email,paymentMethod);
    }

}
