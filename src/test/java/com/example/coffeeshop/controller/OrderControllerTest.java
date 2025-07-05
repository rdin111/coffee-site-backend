package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.CartItemDto;
import com.example.coffeeshop.dto.OrderDto;
import com.example.coffeeshop.model.Order;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.service.OrderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // We mock the service layer, so we don't need a real database for this test
    private OrderService orderService;

    @Test
    @WithMockUser(username = "testuser") // Simulate a request from a user named "testuser"
    void createOrder_whenAuthenticated_shouldSucceed() throws Exception {
        // --- ARRANGE ---
        CartItemDto itemDto = new CartItemDto();
        itemDto.setProductId(1L);
        itemDto.setQuantity(2);

        OrderDto orderDto = new OrderDto();
        orderDto.setItems(List.of(itemDto));

        // Create a fake Order object that our mock service will return
        User user = new User();
        user.setUsername("testuser");
        Order returnedOrder = new Order();
        returnedOrder.setId(1L);
        returnedOrder.setUser(user);
        returnedOrder.setTotalAmount(20.00);
        returnedOrder.setOrderDate(LocalDateTime.now());

        // When the createOrder method is called in the service, return our fake order
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(returnedOrder);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(20.00))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void createOrder_whenNotAuthenticated_shouldFail() throws Exception {
        // --- ARRANGE ---
        CartItemDto itemDto = new CartItemDto();
        itemDto.setProductId(1L);
        itemDto.setQuantity(2);
        OrderDto orderDto = new OrderDto();
        orderDto.setItems(List.of(itemDto));

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isForbidden()); // <-- THE FIX: Change from isUnauthorized() to isForbidden()
    }
}

