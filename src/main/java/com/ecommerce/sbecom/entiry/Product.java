package com.ecommerce.sbecom.entiry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
//@Table(name = "product",indexes = {
//        @Index()
//}
//)
public class Product extends BaseEntity {

    private String productName;
    private String description;
    private Integer quantity;
    private Double price;
    private double specialPrice;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

}
