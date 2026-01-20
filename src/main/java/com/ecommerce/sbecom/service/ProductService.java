package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.dto.ProductRequest;
import com.ecommerce.sbecom.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDto> getAllProducts();
    Product getProductById(UUID id);
    Product getProductByName(String name);
    Product createProduct(ProductRequest product, UUID categoryId);

    List<ProductDto> getAllProductByCategoryId(UUID categoryId);

    List<ProductDto> getProductByKeyWord(String keyword);

    ProductDto updateProduct(UUID productId, ProductRequest productRequest);
}
