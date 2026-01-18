package com.ecommerce.sbecom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role extends BaseModel {

    @Column(nullable = false, unique = true)
    private String name; // ROLE_USER, ROLE_ADMIN
}
