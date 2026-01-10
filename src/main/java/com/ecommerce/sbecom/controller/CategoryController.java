package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.CategoryDto;
import com.ecommerce.sbecom.dto.CategoryRequest;
import com.ecommerce.sbecom.dto.CategoryResponse;
import com.ecommerce.sbecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<Object>> getAllCategory(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "categoryName") String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder

    ) { // ← RETURN TYPE CHANGE
        CategoryResponse categoryResponse = categoryService.getAllCategory(pageNumber, pageSize, sortBy, sortOrder);

        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Categories fetched successfully")
                .data(categoryResponse)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {

        CategoryDto category = categoryService.createCategory(categoryRequest);

        ApiResponse<CategoryDto> response = ApiResponse.<CategoryDto>builder()
                .success(true)
                .message("Category created successfully")
                .data(category)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable UUID id) {

        categoryService.deleteCategory(id);

        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Category deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCategory(@PathVariable("id") UUID id,
            @RequestBody CategoryRequest categoryRequest) {
        CategoryDto updatedCategoryDto = categoryService.updateCategory(id, categoryRequest);

        ApiResponse<Object> response = ApiResponse.builder()
                .message("Categoyr Updated successfully")
                .success(true)
                .data(updatedCategoryDto)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/echo")
    public ResponseEntity<String> echo(@RequestParam(name = "message") String message) {
        return ResponseEntity.status(HttpStatus.OK).body(message);

    }
}
