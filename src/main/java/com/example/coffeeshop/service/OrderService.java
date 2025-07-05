package com.example.coffeeshop.service;

import com.example.coffeeshop.dto.OrderDto;
import com.example.coffeeshop.model.Order;
import com.example.coffeeshop.model.OrderItem;
import com.example.coffeeshop.model.Product;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.ProductRepository;
import com.example.coffeeshop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service // Marks this as a Spring service component
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional // This annotation is very important!
    public Order createOrder(OrderDto orderDto) {
        // 1. Get the currently authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 2. Create the main Order object
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        double totalAmount = 0.0;

        // 3. Create OrderItems from the DTO and calculate total amount
        for (var itemDto : orderDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));

            // 1. Check if there is enough stock
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // 2. Decrement the stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            // No need to call productRepository.save(product) here. Because this method
            // is @Transactional, Hibernate will automatically detect the change and
            // include the product UPDATE in the final transaction.

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice()); // Use the current product price

            order.addOrderItem(orderItem);

            totalAmount += product.getPrice() * itemDto.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        // 4. Save the order and all its items to the database
        return orderRepository.save(order);
    }
}
