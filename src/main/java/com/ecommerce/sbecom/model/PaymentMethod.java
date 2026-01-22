package com.ecommerce.sbecom.model;

public enum PaymentMethod {
    // Core MVP Methods (Start with these 5)
    UPI,
    CREDIT_CARD,
    DEBIT_CARD,
    NET_BANKING,
    COD,

    // Phase 2 (Add after MVP)
    EMI,
    WALLET,
    PAY_LATER,
    GIFT_CARD,

    // International (For future)
    PAYPAL,
    INTERNATIONAL_CARD;
}
