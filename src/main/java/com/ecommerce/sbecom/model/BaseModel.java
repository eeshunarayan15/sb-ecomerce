package com.ecommerce.sbecom.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@MappedSuperclass
@Getter
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
