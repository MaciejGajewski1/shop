package com.simple.shop.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final long id) {
        super("Product for id: " + id + " not found");
    }
}
