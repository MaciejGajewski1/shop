package com.simple.shop.service;

import com.simple.shop.model.Product;
import com.simple.shop.model.ProductDto;
import com.simple.shop.model.ProductResponse;

import java.util.Optional;

public interface ProductService {
    Product createProduct(ProductDto productDto);
    void deleteProduct(Long id);
    Product updateProduct(Long id, ProductDto productDto);
    ProductResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);
    Optional<Product> getProduct(Long id);
}
