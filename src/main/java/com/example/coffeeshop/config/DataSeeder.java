package com.example.coffeeshop.config;

import com.example.coffeeshop.model.Product;
import com.example.coffeeshop.repository.ProductRepository;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.repository.UserRepository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader; // To load files from the classpath
    private final ObjectMapper objectMapper; // To parse JSON

    public DataSeeder(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ResourceLoader resourceLoader) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper(); // Jackson's JSON parser
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User demoAdmin = new User();
            demoAdmin.setUsername("demouser");
            demoAdmin.setEmail("demo@user.com");
            demoAdmin.setPassword(passwordEncoder.encode("password"));
            demoAdmin.setRoles("ROLE_USER,ROLE_ADMIN");
            userRepository.save(demoAdmin);
            System.out.println("--- Demo admin user seeded ---");
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            try {
                // Load the JSON file from the classpath resources
                Resource resource = resourceLoader.getResource("classpath:products.json");
                InputStream inputStream = resource.getInputStream();

                // Use Jackson's ObjectMapper to read the file and convert it into a List of Products
                List<Product> products = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});

                // Save all the products to the database
                productRepository.saveAll(products);

                System.out.println("--- Database seeded with " + products.size() + " products from JSON ---");
            } catch (Exception e) {
                System.out.println("!!! Unable to seed products from JSON file: " + e.getMessage());
            }
        } else {
            System.out.println("--- Product database already contains data. Seeding not required. ---");
        }
    }
}
