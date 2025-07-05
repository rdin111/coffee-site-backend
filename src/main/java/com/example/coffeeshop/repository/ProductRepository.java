package com.example.coffeeshop.repository;

import com.example.coffeeshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductRepository extends JpaRepository<Product,Long> {
    // This is our new search method.
    // Spring Data JPA will automatically generate a query that looks for a keyword
    // in the 'name' OR 'description' fields, ignoring case.
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);


}
