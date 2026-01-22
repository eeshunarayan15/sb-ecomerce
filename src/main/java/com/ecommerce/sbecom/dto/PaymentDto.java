package com.ecommerce.sbecom.dto;

import com.ecommerce.sbecom.model.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    @NotNull(message = "Payment ID cannot be null")
    private UUID id;

    @NotBlank(message = "Payment method cannot be blank")
    @Size(min = 2, max = 50, message = "Payment method must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9_\\s-]+$", message = "Payment method can only contain letters, numbers, spaces, hyphens and underscores")
    private String paymentMethod;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "1000000.00", message = "Amount cannot exceed 1,000,000")
    @Digits(integer = 7, fraction = 2, message = "Amount must have up to 7 integer digits and 2 decimal places")
    private Double amount;

    @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Transaction ID can only contain letters, numbers, hyphens and underscores")
    private String transactionId;

    @Size(max = 100, message = "PG Payment ID cannot exceed 100 characters")
    private String pgPaymentId;

    @Size(max = 50, message = "PG Status cannot exceed 50 characters")
    private String pgStatus;

    @Size(max = 500, message = "PG Response Message cannot exceed 500 characters")
    private String pgResponseMessage;

    @Size(max = 50, message = "PG Name cannot exceed 50 characters")
    private String pgName;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;

    @Valid
    @NotNull(message = "Order information cannot be null")
    private OrderDto order;

    @NotNull(message = "Created date cannot be null")
    @PastOrPresent(message = "Created date must be in the past or present")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date cannot be null")
    @PastOrPresent(message = "Updated date must be in the past or present")
    private LocalDateTime updatedAt;

}
