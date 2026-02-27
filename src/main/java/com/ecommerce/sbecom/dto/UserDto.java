package com.ecommerce.sbecom.dto;


import com.ecommerce.sbecom.model.Provider;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;

    private String username;
    private String email;

    private String fullName;


    private String phone;

    private Provider provider;
    private boolean enabled;

    private LocalDateTime createdAt;

    // Optional
    private AddressDto address;

    // Role names only (clean & safe)
    private List<String> roles;
}
