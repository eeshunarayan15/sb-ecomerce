package com.ecommerce.sbecom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_token", indexes = {
        @Index(name = "refresh_token_jti_jdx", columnList = "jti", unique = true),
        @Index(name = "refresh_token_user_id_idx", columnList = "user_id")
})
public class RefreshToken extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    @Column(name = "jti", nullable = false, unique = true, updatable = false)
    private String jti;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiredAt;
    private boolean revoked;
    private String refreshString;
    private String replacedByToken;
    private String token;
}
