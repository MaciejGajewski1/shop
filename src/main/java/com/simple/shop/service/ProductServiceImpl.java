package com.simple.shop.service;


import com.simple.shop.model.Product;
import com.simple.shop.model.ProductDto;
import com.simple.shop.model.ProductResponse;
import com.simple.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Service
class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setPrice(new BigDecimal(productDto.getPrice()));
        productRepository.save(product);
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        productRepository.delete(product);
    }

    @Override
    public Product updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setTitle(productDto.getTitle());
        product.setPrice(new BigDecimal(productDto.getPrice()));
        productRepository.save(product);
        return product;
    }

    @Override
    public ProductResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findAll(pageable);

        List<Product> listOfProducts = products.getContent();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductList(listOfProducts);
        productResponse.setPageNo(products.getNumber());
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setLast(products.isLast());

        return productResponse;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }
}
