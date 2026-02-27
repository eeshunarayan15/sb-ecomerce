package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.ApiResponse;
import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.dto.ProductRequest;
import com.ecommerce.sbecom.dto.ProductResponse;
import com.ecommerce.sbecom.model.Product;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.service.AuthService;
import com.ecommerce.sbecom.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ApiResponse<Object>> createProduct(@Valid @RequestBody ProductRequest productRequest,
                                                             @PathVariable(required = true) UUID categoryId) {
        Product product = productService.createProduct(productRequest, categoryId);


        ProductDto data = ProductDto.builder()
                .productId(product.getId().toString())
                .productName(product.getProductName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .specialPrice(product.getSpecialPrice())
                .image(product.getImage())
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
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(required = false)String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc")  String sortDir
    ) {
        log.info(
                "GET /public/product called with page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir
        );

        System.out.println("page = " + page + " size = " + size + " sortBy = " + sortBy + " sortDir = " + sortDir+" category= "+category);
        ProductResponse response = productService.getAllProducts(page, size, sortBy, sortDir,keyword,category);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ApiResponse<Object>> getProductById(@PathVariable UUID productId) {
        ProductDto product = productService.getProductById(productId); // implement in service
        return ResponseEntity.ok(ApiResponse.builder()
                .data(product)
                .message("Product found")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
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
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<Object>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ProductRequest productRequest
    ) {
        ProductDto updated = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Product updated successfully")
                .success(true)
                .data(updated)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Product deleted successfully")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
