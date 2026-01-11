package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CategoryDto;
import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.dto.ProductRequest;
import com.ecommerce.sbecom.entiry.Category;
import com.ecommerce.sbecom.entiry.Product;
import com.ecommerce.sbecom.repository.CategoryRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> all = productRepository.findAll();
        return all.stream().map(p ->
                ProductDto.builder()
                        .productId(p.getId().toString())
                        .productName(p.getProductName())
                        .categoryDto(CategoryDto.builder()
                                .id(p.getCategory().getId())
                                .categoryName(p.getCategory().getCategoryName())
                                .build())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .specialPrice(p.getSpecialPrice())
                        .quantity(p.getQuantity())


                        .build()
        ).toList();

    }

    @Override
    public Product getProductById(UUID id) {
        return null;
    }

    @Override
    public Product getProductByName(String name) {
        return null;
    }

    @Override
    public Product createProduct(ProductRequest productRequest, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with category id :" + categoryId));


        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .category(category)
                .specialPrice(productRequest.getSpecialPrice())
                .build();

        return productRepository.save(product);
    }

    @Override
    public List<ProductDto> getAllProductByCategoryId(UUID categoryId) {
        List<Product> byCategoryId = productRepository.findByCategoryId(categoryId);
        List<ProductDto> list = byCategoryId.stream().map(p ->
                ProductDto.builder()
                        .productName(p.getProductName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .specialPrice(p.getSpecialPrice())
                        .quantity(p.getQuantity())
                        .productId(p.getId().toString())


                        .build()
        ).toList();


        return list;
    }

    @Override
    public List<ProductDto> getProductByKeyWord(String keyword) {
        List<Product> productList = productRepository.findByProductNameLikeIgnoreCase(keyword);
        return productList.stream().map(product ->
                ProductDto.builder()
                        .productId(product.getId().toString())
                        .productName(product.getProductName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .specialPrice(product.getSpecialPrice())
                        .quantity(product.getQuantity())
                        .categoryDto(CategoryDto.builder()
                                .id(product.getCategory().getId())
                                .categoryName(product.getCategory().getCategoryName())
                                .build())
                        .build()).toList();
    }
}
