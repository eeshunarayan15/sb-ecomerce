package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.model.Order;
import com.ecommerce.sbecom.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    private OrderStatus orderStatus;
}
