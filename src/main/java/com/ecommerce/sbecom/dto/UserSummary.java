package com.ecommerce.sbecom.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummary {private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;

}
