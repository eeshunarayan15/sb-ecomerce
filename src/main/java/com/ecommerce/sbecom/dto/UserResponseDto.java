package com.ecommerce.sbecom.dto;


import com.ecommerce.sbecom.model.Provider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private String phone;

    private AddressDto address;

    private Provider provider;
    private boolean enabled;

    private Set<String> roles;

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
