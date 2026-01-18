package com.ecommerce.sbecom.model;

public enum CartStatus {
    ACTIVE,              // User is currently shopping
    ABANDONED,           // User left without checkout (send reminder)
    CHECKOUT_IN_PROGRESS, // User is on checkout page
    CONVERTED_TO_ORDER,  // Successfully placed order
    EXPIRED,             // Auto-deleted after 30 days of inactivity
    SAVED_FOR_LATER      // User saved cart for future (Flipkart/Amazon feature)
}
