package com.example.coffeeshop.service;


import com.example.coffeeshop.dto.CartItemDto;
import com.example.coffeeshop.dto.OrderDto;
import com.example.coffeeshop.model.Order;
import com.example.coffeeshop.model.Product;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.ProductRepository;
import com.example.coffeeshop.repository.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // This integrates Mockito with JUnit 5
class OrderServiceTest {

    @Mock // Creates a mock instance of ProductRepository. It will not use the real database.
    private ProductRepository productRepository;

    @Mock // Creates a mock instance of UserRepository
    private UserRepository userRepository;

    @Mock // Creates a mock instance of OrderRepository
    private OrderRepository orderRepository;

    @InjectMocks // Creates an instance of OrderService and injects the mocks defined above into it
    private OrderService orderService;

    // This setup runs before each test to mock the security context
    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createOrder_shouldCalculateTotalAmountCorrectly() {
        // --- ARRANGE ---
        // 1. Define the input data for our test
        CartItemDto item1 = new CartItemDto();
        item1.setProductId(1L);
        item1.setQuantity(2);

        CartItemDto item2 = new CartItemDto();
        item2.setProductId(2L);
        item2.setQuantity(1);

        OrderDto orderDto = new OrderDto();
        orderDto.setItems(List.of(item1, item2));

        // 2. Define the behavior of our mocks
        User testUser = new User(); // A fake user
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRoles("ROLE_USER");

        Product product1 = new Product(); // A fake product
        product1.setId(1L);
        product1.setPrice(10.00);
        product1.setStockQuantity(100);

        Product product2 = new Product(); // Another fake product
        product2.setId(2L);
        product2.setPrice(5.50);
        product2.setStockQuantity(100);

        // Tell Mockito: "When findByUsername is called with 'testuser', return our fake user"
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        // Tell Mockito: "When findById is called with 1L, return our fake product1"
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        // Tell Mockito: "When findById is called with 2L, return our fake product2"
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        // Tell Mockito: "When save is called with any Order object, just return that same object"
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        // Call the actual method we want to test
        Order resultOrder = orderService.createOrder(orderDto);

        // --- ASSERT ---
        // Check if the results are what we expect
        Assertions.assertNotNull(resultOrder);
        // The total should be (2 * 10.00) + (1 * 5.50) = 25.50
        Assertions.assertEquals(25.50, resultOrder.getTotalAmount());
        Assertions.assertEquals(2, resultOrder.getItems().size());
    }
}
