package com.ecommerce.sbecom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class Order extends BaseModel {
    @Builder.Default
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<OrderItem> orderItemList = new ArrayList<>();
    private String email;
    private LocalDateTime orderDateTime;
    private double totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "order")
    @ToString.Exclude
    private Payment payment;
}
