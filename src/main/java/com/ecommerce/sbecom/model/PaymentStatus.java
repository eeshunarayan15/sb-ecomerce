package com.ecommerce.sbecom.model;

public enum PaymentStatus {
    PENDING,          // UPI/Net Banking initiated
    SUCCESSFUL,       // Payment successful
    FAILED,           // Payment failed
    COD_PENDING,      // Cash on Delivery selected
    COD_COLLECTED,    // Cash collected
    COD_FAILED,       // Cash not collected
    REFUND_INITIATED  // Return/refund started
}
