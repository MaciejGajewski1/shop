package com.simple.shop.service;

import com.simple.shop.exceptions.FullCartException;
import com.simple.shop.model.Cart;
import com.simple.shop.model.PriceDto;
import com.simple.shop.model.Product;
import com.simple.shop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
class CartServiceImpl implements CartService {
    private CartRepository cartRepository;
    private ProductService productService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    public Cart createCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart addProduct(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (cart.getProducts().size() >= 3) {
            throw new FullCartException();
        }
        Product product = productService.getProduct(productId).orElseThrow();
        cart.addProduct(product);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart deleteProduct(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productService.getProduct(productId).orElseThrow();
        cart.removeProduct(product);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Set<Product> getAllProducts(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        return cart.getProducts();
    }

    @Override
    public PriceDto getSummarizedPrice(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        BigDecimal result = new BigDecimal(0);
        for (Product product : cart.getProducts()) {
            result = result.add(product.getPrice());
        }
        return new PriceDto(result);
    }

}
