package com.example.coffeeshop.repository;

import com.example.coffeeshop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // We don't need any custom methods for now.
    // JpaRepository gives us everything we need.
}
