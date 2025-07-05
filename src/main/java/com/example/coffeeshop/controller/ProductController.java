package com.example.coffeeshop.controller;

import com.example.coffeeshop.model.Product;
import com.example.coffeeshop.repository.ProductRepository;
import com.example.coffeeshop.dto.PageDto;
import org.springframework.cache.annotation.Cacheable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @Cacheable("products")
    // Change the return type to our new DTO
    public PageDto<Product> getAllProducts(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        Page<Product> productPage; // A temporary variable to hold the result

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        // Map the Spring Page object to our clean PageDto before returning
        return new PageDto<>(productPage);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(@Valid @RequestBody Product product) {
        return productRepository.save(product);
    }
}
