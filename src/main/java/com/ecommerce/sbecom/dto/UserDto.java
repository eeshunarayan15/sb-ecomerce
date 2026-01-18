package com.ecommerce.sbecom.dto;


import com.ecommerce.sbecom.model.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String id;

    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private String phone;

    private Provider provider;
    private boolean enabled;

    private LocalDateTime createdAt;

    // Optional
    private AddressDto address;

    // Role names only (clean & safe)
    private Set<String> roles;
}
