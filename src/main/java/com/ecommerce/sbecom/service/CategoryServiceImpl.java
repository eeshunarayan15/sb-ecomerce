package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CategoryDto;
import com.ecommerce.sbecom.dto.CategoryRequest;
import com.ecommerce.sbecom.dto.CategoryResponse;
import com.ecommerce.sbecom.model.Category;
import com.ecommerce.sbecom.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse getAllCategory(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize,sortByAndOrder);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> content = categoryPage.getContent();

        List<CategoryDto> list = content.stream().map(c -> CategoryDto.builder()
                .categoryName(c.getCategoryName())
                .id(c.getId())
                .build()).toList();

        return CategoryResponse.builder()
                .data(list)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .lastPage(categoryPage.isLast())
                .build();

    }

    @Override
    public CategoryDto createCategory(CategoryRequest categoryRequest) {

        Category category = categoryRepository.save(Category.builder()
                .categoryName(categoryRequest.getCategoryName())
                .build());
        return CategoryDto.builder().categoryName(category.getCategoryName()).id(category.getId()).build();

    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto updateCategory(UUID id, CategoryRequest categoryRequest) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setCategoryName(categoryRequest.getCategoryName());

        Category savedCategory = categoryRepository.save(category);

        return CategoryDto.builder().categoryName(savedCategory.getCategoryName()).id(savedCategory.getId()).build();

    }
}
