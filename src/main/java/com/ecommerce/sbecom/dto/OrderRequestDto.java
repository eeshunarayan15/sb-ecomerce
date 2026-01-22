package com.ecommerce.sbecom.dto;

import com.ecommerce.sbecom.model.PaymentMethod;
import com.ecommerce.sbecom.model.PaymentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    private UUID addressId;
    private PaymentMethod paymentMethod;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private PaymentStatus paymentStatus;

}
