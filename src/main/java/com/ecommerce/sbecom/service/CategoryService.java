package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CategoryDto;
import com.ecommerce.sbecom.dto.CategoryRequest;
import com.ecommerce.sbecom.dto.CategoryResponse;
import com.ecommerce.sbecom.entiry.Category;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
  CategoryResponse getAllCategory(int pageNumber, int pageSize, String sortBy, String sortOrder);

   CategoryDto createCategory(@Valid CategoryRequest categoryRequest);

   void deleteCategory(UUID id);

   CategoryDto updateCategory(UUID id, CategoryRequest categoryRequest);
}
