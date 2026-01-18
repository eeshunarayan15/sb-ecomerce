package com.ecommerce.sbecom.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    
    LocalDateTime expiresIn;
    String tokenType;
    UserDto userDto;
}
