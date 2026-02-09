package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CategoryDto;
import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.dto.ProductRequest;
import com.ecommerce.sbecom.dto.ProductResponse;
import com.ecommerce.sbecom.exception.APIException;
import com.ecommerce.sbecom.exception.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Category;
import com.ecommerce.sbecom.model.Product;
import com.ecommerce.sbecom.repository.CategoryRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productName",
            "price",
            "specialPrice",
            "quantity",
            "createdAt"
    );

    @Transactional
    @Override
    public ProductResponse getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String keyword,
            String category
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
//        Specification<Product> spec=Specification.where((Specification<Product>) null);
//        Specification<Product> spec = Specification.where((Specification<Product>) null);
//        Specification<Product> spec = Specification.where(null);
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like((root.get("category").get("categoryName")), category));
        }

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<Product> content = productPage.getContent();
        List<ProductDto> list = content.stream().map(p ->
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
                        .image(p.getImage())
                        .build()
        ).toList();

        return ProductResponse.builder()
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .empty(productPage.isEmpty())
                .numberOfElements(productPage.getNumberOfElements())
                .content(list)
                .build();


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
                .image(productRequest.getImage())

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

    @Transactional
    @Override
    public ProductDto updateProduct(UUID productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not found with id : "
                        + productId));

        if (productRequest.getPrice() != null &&
                productRequest.getSpecialPrice() != null &&
                productRequest.getSpecialPrice() > productRequest.getPrice()) {
            throw new APIException("Special price cannot be greater than price");
        }

        if (productRequest.getQuantity() != null && productRequest.getQuantity() < 0) {
            throw new APIException("Quantity cannot be negative");
        }
        if (productRequest.getProductName() != null) {
            product.setProductName(productRequest.getProductName());
        }

        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }

        if (productRequest.getPrice() != null) {
            product.setPrice(productRequest.getPrice());
        }

        if (productRequest.getSpecialPrice() != null) {
            product.setSpecialPrice(productRequest.getSpecialPrice());
        }

        if (productRequest.getQuantity() != null) {
            product.setQuantity(productRequest.getQuantity());
        }
        Product updatedProduct = productRepository.save(product);
        return ProductDto.builder()
                .productId(updatedProduct.getId().toString())
                .productName(updatedProduct.getProductName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .specialPrice(updatedProduct.getSpecialPrice())
                .quantity(updatedProduct.getQuantity())
                .categoryDto(CategoryDto.builder()
                        .id(updatedProduct.getCategory().getId())
                        .categoryName(updatedProduct.getCategory().getCategoryName())
                        .build())
                .build();

    }
}
