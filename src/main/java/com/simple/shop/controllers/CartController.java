package com.simple.shop.controllers;

import com.simple.shop.exceptions.FullCartException;
import com.simple.shop.model.Cart;
import com.simple.shop.model.PriceDto;
import com.simple.shop.model.Product;
import com.simple.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class CartController {

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/carts")
    public ResponseEntity<Cart> createCart() {
        Cart cart = cartService.createCart();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cart);
    }

    @PatchMapping("carts/{cartId}/products/add/{productId}")
    public ResponseEntity<Object> addProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.addProduct(cartId, productId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (FullCartException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PatchMapping("carts/{cartId}/products/delete/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.deleteProduct(cartId, productId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("carts/{id}")
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProducts(@PathVariable Long id) {
        try {
            List<EntityModel<Product>> products = cartService.getAllProducts(id)
                    .stream()
                    .map(product -> EntityModel.of(
                            product,
                            linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products")
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .body(CollectionModel.of(
                            products,
                            linkTo(methodOn(CartController.class).getAllProducts(id)).withSelfRel(),
                            linkTo(methodOn(CartController.class).getSummarizedPrice(id)).withRel("price_summarized")
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("carts/{id}/price")
    public ResponseEntity<PriceDto> getSummarizedPrice(@PathVariable Long id) {
        try {
            PriceDto priceDto = cartService.getSummarizedPrice(id);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .body(priceDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}
