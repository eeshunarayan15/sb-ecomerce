package com.ecommerce.sbecom.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Carts")
public class Cart extends BaseModel{
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @OneToMany(mappedBy = "cart")
    @Builder.Default
    private List<CartItem> cartItemList=new ArrayList<>();

    @Builder.Default
    private Double subtotal = 0.0; // Sum of all items before tax/discount

    @Builder.Default
    private Double discount = 0.0; // Total discount applied

    // ========== GST FIELDS (IMPORTANT FOR INDIA) ==========
    @Builder.Default
    private Double cgst = 0.0; // Central GST

    @Builder.Default
    private Double sgst = 0.0; // State GST (same state delivery)

    @Builder.Default
    private Double igst = 0.0; // Integrated GST (inter-state delivery)

    @Builder.Default
    private Double totalGst = 0.0; // Total GST (CGST + SGST or IGST)

    @Builder.Default
    private Double gstPercentage = 18.0; // Applicable GST rate (default 18%)

    // ========== FINAL PRICING ==========
    @Builder.Default
    private Double shippingCost = 0.0; // Delivery charges

    @Builder.Default
    private Double totalPrice = 0.0; // subtotal + totalGst + shippingCost - discount

    @Builder.Default
    private Integer totalItems = 0; // Count of items

    // ========== COUPON & OFFERS ==========
    private String couponCode; // Applied coupon code

    @Builder.Default
    private Double couponDiscount = 0.0; // Discount from coupon

    @Builder.Default
    private Double productDiscount = 0.0; // Discount on products themselves

    // ========== DELIVERY INFO ==========
    private String deliveryState; // Karnataka, Maharashtra, etc.

    private String deliveryPincode; // 560001, 400001, etc.

    @Builder.Default
    private Boolean isEligibleForFreeShipping = false; // Free delivery on ₹500+

    private Integer estimatedDeliveryDays; // 2-3 days, 5-7 days

    // ========== STATUS ==========
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    @Builder.Default
    private Boolean isActive = true;

    // ========== PAYMENT INFO ==========
    @Builder.Default
    private Boolean codAvailable = true; // Cash on Delivery available?

    @Builder.Default
    private Double codCharges = 0.0; // COD charges (if any)

    // ========== HELPER METHODS ==========
}
