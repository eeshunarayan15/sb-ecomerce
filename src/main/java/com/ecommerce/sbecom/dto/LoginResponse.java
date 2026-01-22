package com.ecommerce.sbecom.dto;


import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    LocalDateTime expiresIn;
    String tokenType;
    UserDto userDto;
}
