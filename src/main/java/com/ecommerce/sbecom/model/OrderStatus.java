package com.ecommerce.sbecom.model;

public enum OrderStatus {
    // 12 statuses for most e-commerce
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    CONFIRMED,
    PROCESSING,
    READY_TO_SHIP,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELIVERY_FAILED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURN_COMPLETED,
    ACCEPTED
}
