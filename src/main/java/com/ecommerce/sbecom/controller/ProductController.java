package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.dto.ProductRequest;
import com.ecommerce.sbecom.dto.ProductResponse;
import com.ecommerce.sbecom.entiry.Product;
import com.ecommerce.sbecom.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ApiResponse<Object>> createProduct(@Valid @RequestBody ProductRequest productRequest,
                                                             @PathVariable(required = true) UUID categoryId) {
        Product product = productService.createProduct(productRequest, categoryId);


        ProductDto data = ProductDto.builder()

                .productName(product.getProductName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .specialPrice(product.getSpecialPrice())
                .build();


        ApiResponse<Object> res = ApiResponse.builder()
                .message("Product created successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/public/product")
    public ResponseEntity<ProductResponse> getAllProducts() {
        List<ProductDto> allProducts = productService.getAllProducts();
        ProductResponse res = ProductResponse.builder()
                .content(allProducts)

                .build();
        return ResponseEntity.status(HttpStatus.OK).body(res);

    }

    public void getProductById(@PathVariable UUID id) {
    }

    public void getProductByName(String name) {
    }

    public void getAllProductsByCategory(String category) {
    }

    @GetMapping("/product/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> getAllProductsByCategoryId(@PathVariable UUID categoryId) {
        List<ProductDto> allProductByCategoryId = productService.getAllProductByCategoryId(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()

                .data(ProductResponse.builder()
                        .content(allProductByCategoryId)

                        .build())
                .message("Product found successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    public void getAllProductsByCategoryName() {
    }

    public void getAllProductsByPrice() {
    }

    public void getAllProductsByPriceId() {
    }

    public void getAllProductsByPriceName() {
    }

    public void getAllProductsByPriceCategory() {
    }

    public void getAllProductsByPriceCategoryId() {
    }

    public void getAllProduct() {
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ApiResponse<Object>> getProductByKeyWord(@PathVariable String keyword) {
        List<ProductDto> productByKeyWord = productService.getProductByKeyWord(keyword);
        ProductResponse res = ProductResponse.builder()
                .content(productByKeyWord).build();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .success(true)
                .timestamp(LocalDateTime.now().toString()).message("Product Found SucessFully")
                .data(res)
                .build());
    }
}
