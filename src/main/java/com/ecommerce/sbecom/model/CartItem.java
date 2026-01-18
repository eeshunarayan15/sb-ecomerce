package com.ecommerce.sbecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "cart_items")
public class CartItem extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


    // ========== QUANTITY ==========
    private Integer quantity; // Number of items (1, 2, 3, etc.)

    private Integer maxQuantity; // Max allowed per user (e.g., 5 per customer)

    // ========== PRICING (IMPORTANT) ==========
    private Double mrp; // Maximum Retail Price (₹1000)

    private Double sellingPrice; // Actual price (₹800)

    private Double discount; // Discount amount per item (₹200)

    private Double discountPercentage; // 20% OFF, 30% OFF

    private Double subtotal; // quantity × sellingPrice (before GST)

    // ========== GST FIELDS (FOR INDIA) ==========
    private Double gstRate; // 5%, 12%, 18%, 28%

    private Double cgst; // Central GST amount

    private Double sgst; // State GST amount

    private Double igst; // Integrated GST amount

    private Double totalGst; // Total GST for this item

    private String hsnCode; // HSN code for GST invoice (e.g., "6109")

    // ========== FINAL PRICE ==========
    private Double totalPrice; // subtotal + totalGst (final item price)

    // ========== PRODUCT SNAPSHOT (IMPORTANT!) ==========
    private String productName; // Store name at time of adding

    private String productImage; // Store image URL

    private String productDescription; // Brief description

    // ========== SELLER INFO (FOR MARKETPLACE) ==========
    private String sellerName; // "Cloudtail India" (Amazon), "RetailNet" (Flipkart)

    private String sellerGstin; // Seller's GST number

    private Long sellerId; // Reference to seller

    // ========== STOCK & AVAILABILITY ==========
    private Boolean inStock; // Is product currently in stock?

    private Integer availableQuantity; // How many left in stock

    // ========== DELIVERY INFO ==========
    private Boolean codAvailable; // Cash on Delivery available?

    private Integer estimatedDeliveryDays; // 2-3 days, 5-7 days

    private String deliveryMessage; // "Get it by Monday, Jan 20"

    // ========== OFFERS & COUPONS ==========
    private String appliedOffer; // "Buy 2 Get 1 Free"

    private Boolean isCouponApplicable; // Can coupon be applied?

    // ========== GIFT & CUSTOMIZATION ==========
    private Boolean isGift; // Is this a gift?

    private String giftMessage; // Gift message text

    private String size; // S, M, L, XL (for clothing)

    private String color; // Red, Blue, etc.

    // ========== SAVE FOR LATER ==========
    private Boolean isSavedForLater; // Moved to "Save for Later"

    // ========== TIMESTAMPS (Already in BaseModel) ==========
    // createdAt, updatedAt from BaseModel


}
