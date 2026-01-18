package com.ecommerce.sbecom.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "category", indexes = {
                @Index(name = "idx_category_name", columnList = "categoryName")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category extends BaseModel {
        @Column(unique = true)
        private String categoryName;
        @OneToMany(mappedBy = "category")
        @Builder.Default
        @JsonManagedReference
        private List<Product> products =new ArrayList<>();

}
