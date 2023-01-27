package com.simple.shop.service;

import com.simple.shop.model.Product;
import com.simple.shop.model.ProductDto;
import com.simple.shop.model.ProductResponse;
import com.simple.shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    public static final long productId = 1;
    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void shouldUpdateProduct() {
        // given
        Product product = new Product();
        product.setTitle("Product1");
        product.setPrice(new BigDecimal("15.99"));
        ProductDto productDto = new ProductDto("Product2", "7.45");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        // when
        Product testProduct = productService.updateProduct(productId, productDto);
        // then
        assertThat(testProduct.getTitle()).isEqualTo("Product2");
        assertThat(testProduct.getPrice()).isEqualTo(new BigDecimal("7.45"));
    }

    @Test
    void shouldGetFirstPageOfProducts() {
        // given
        List<Product> products = new ArrayList<>(List.of(
                new Product(),
                new Product(),
                new Product(),
                new Product()
        ));
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);
        Page<Product> pagedProducts = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findAll(ArgumentMatchers.isA(Pageable.class))).thenReturn(pagedProducts);
        // when
        ProductResponse firstPage = productService.getAllProducts(0, 3, "id", "desc");
        // then
        assertThat(firstPage.getPageSize()).isEqualTo(2);
        assertThat(firstPage.getPageNo()).isEqualTo(0);
        assertThat(firstPage.isLast()).isFalse();
    }

    @Test
    void shouldGetLastPageOfProducts() {
        // given
        List<Product> products = new ArrayList<>(List.of(
                new Product(),
                new Product(),
                new Product(),
                new Product()
        ));
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        Page<Product> pagedProducts = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findAll(ArgumentMatchers.isA(Pageable.class))).thenReturn(pagedProducts);
        // when
        ProductResponse lastPage = productService.getAllProducts(1, 3, "id", "desc");
        // then
        assertThat(lastPage.getPageSize()).isEqualTo(2);
        assertThat(lastPage.getPageNo()).isEqualTo(1);
        assertThat(lastPage.isLast()).isTrue();
    }
}