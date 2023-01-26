package com.simple.shop.service;

import com.simple.shop.model.Cart;
import com.simple.shop.model.PriceDto;
import com.simple.shop.model.Product;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface CartService {
    Cart createCart();
    Cart addProduct(Long cartId, Long productId);
    Cart deleteProduct(Long cartId,Long productId);
    Set<Product> getAllProducts(Long cartId);
    PriceDto getSummarizedPrice(Long cartId);

}
