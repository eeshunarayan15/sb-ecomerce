package com.ecommerce.sbecom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "payments")
public class Payment extends BaseModel{

    private PaymentMethod paymentMethod;



    private double amount;

    private String transactionId;

    private String pgPaymentId;

    private String pgStatus;

    private String pgResponseMessage;

    private String pgName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "order_id",unique = true,nullable = false)
    @JsonBackReference
    private Order order;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public Payment(UUID paymentId ,String pgPaymentId   ,String pgStatus,String pgResponseMessage,String pgName) {
//         this.paymentId = paymentId;
      
         this.pgStatus = pgStatus;
         this.pgResponseMessage = pgResponseMessage;
         this.pgName = pgName;
         this.paymentStatus = PaymentStatus.PENDING;
         this.createdAt = LocalDateTime.now();
         this.updatedAt = LocalDateTime.now();
    }
}
