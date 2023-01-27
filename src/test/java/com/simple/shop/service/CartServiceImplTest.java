package com.simple.shop.service;

import com.simple.shop.exceptions.FullCartException;
import com.simple.shop.model.Cart;
import com.simple.shop.model.PriceDto;
import com.simple.shop.model.Product;
import com.simple.shop.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    public static final long cartId = 1;
    public static final long productId = 1;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductService productService;
    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void shouldAddProductWhenCartIsNotFull() {
        // given
        Cart cart = new Cart();
        cart.addProduct(new Product());
        cart.addProduct(new Product());
        Product product = new Product();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productService.getProduct(productId)).thenReturn(Optional.of(product));
        // when
        Cart testCart = cartService.addProduct(cartId, productId);
        // then
        assertThat(testCart.getProducts()).hasSize(3);
    }

    @Test
    void shouldNotAddProductWhenCartIsFull() {
        // given
        Cart cart = new Cart();
        cart.addProduct(new Product());
        cart.addProduct(new Product());
        cart.addProduct(new Product());
        Product product = new Product();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        // then
        assertThatThrownBy(() -> cartService.addProduct(cartId, productId))
                .isInstanceOf(FullCartException.class);
    }

    @Test
    void shouldDeleteProduct() {
        // given
        Cart cart = new Cart();
        Product product = new Product();
        cart.addProduct(product);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productService.getProduct(productId)).thenReturn(Optional.of(product));
        // when
        Cart testCart = cartService.deleteProduct(cartId, productId);
        // then
        assertThat(testCart.getProducts()).hasSize(0);
    }

    @Test
    void shouldGetAllProducts() {
        // given
        Cart cart = new Cart();
        Product product1 = new Product();
        Product product2 = new Product();
        product1.setTitle("productTitle");
        product2.setTitle("productTitle");
        cart.addProduct(product1);
        cart.addProduct(product2);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        // when
        Set<Product> allProducts = cartService.getAllProducts(cartId);
        // then
        assertThat(allProducts)
                .hasSize(2)
                .extracting("title").containsOnly("productTitle");
    }

    @Test
    void shouldGetSummarizedPrice() {
        // given
        Cart cart = new Cart();
        Product product1 = new Product();
        Product product2 = new Product();
        product1.setPrice(new BigDecimal("15.75"));
        product2.setPrice(new BigDecimal("14.25"));
        cart.addProduct(product1);
        cart.addProduct(product2);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        // when
        PriceDto summarizedPrice = cartService.getSummarizedPrice(cartId);
        // then
        assertThat(summarizedPrice.getSummmarizedPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

}