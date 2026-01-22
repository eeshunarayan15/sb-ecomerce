package com.ecommerce.sbecom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotNull(message = "Category name cannot be null")
    @NotBlank(message = "Category name cannot be empty or blank")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String categoryName;

}
