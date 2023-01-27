package com.simple.shop.controllers;


import com.simple.shop.model.Product;
import com.simple.shop.model.ProductDto;
import com.simple.shop.model.ProductResponse;
import com.simple.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.NoSuchElementException;

import static com.simple.shop.config.PaginationConfig.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class ProductController {


    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody ProductDto productDto) {
        Product product = productService.createProduct(productDto);
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/products/{id}")
                .buildAndExpand(product.getId());
        return ResponseEntity
                .created(uriComponents.toUri())
                .body(EntityModel.of(
                        product,
                        linkTo(methodOn(ProductController.class).createProduct(productDto)).withSelfRel(),
                        linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("getProduct"),
                        linkTo(methodOn(ProductController.class).getAllProducts(
                                Integer.parseInt(DEFAULT_PAGE_NUMBER),
                                Integer.parseInt(DEFAULT_PAGE_SIZE),
                                DEFAULT_SORT_BY,
                                DEFAULT_SORT_DIRECTION
                        )).withRel("getAllProducts")
                ));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            productService.updateProduct(id, productDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("/products")
    public EntityModel<ProductResponse> getAllProducts (
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "pageDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        ProductResponse productResponse = productService.getAllProducts(pageNo, pageSize, sortBy, sortDir);
        if (pageNo == 0) {
            return EntityModel.of(
                    productResponse,
                    linkTo(methodOn(ProductController.class).getAllProducts(pageNo + 1, pageSize, sortBy, sortDir)).withRel("next_page")
            );
        } else if (productResponse.getTotalPages() - 1 == pageNo) {
            return EntityModel.of(
                    productResponse,
                    linkTo(methodOn(ProductController.class).getAllProducts(pageNo - 1, pageSize, sortBy, sortDir)).withRel("previous_page")
            );
        } else {
            return EntityModel.of(
                    productResponse,
                    linkTo(methodOn(ProductController.class).getAllProducts(pageNo + 1, pageSize, sortBy, sortDir)).withRel("next_page"),
                    linkTo(methodOn(ProductController.class).getAllProducts(pageNo - 1, pageSize, sortBy, sortDir)).withRel("previous_page")
            );
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.of(productService.getProduct(id));
    }

}
