package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.LoginDto;
import com.example.coffeeshop.dto.RegisterDto;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.repository.UserRepository;
import com.example.coffeeshop.security.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Helper to convert objects to JSON strings

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Test
    void registerUser_shouldReturnSuccessMessage() throws Exception {
        // --- ARRANGE ---
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newuser");
        registerDto.setEmail("newuser@example.com");
        registerDto.setPassword("password123");

        // When the repository checks if the username exists, return nothing (it's available)
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        // When the password encoder is called, just return a dummy hashed password
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        // When the repository saves the new user, just return the user object
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))) // Convert DTO to JSON
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void authenticateUser_shouldReturnJwtToken() throws Exception {
        // --- ARRANGE ---
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        String dummyJwt = "dummy.jwt.token";

        // We need to mock the Authentication object that the manager returns
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // When the authentication manager is called, return our mock authentication object
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        // When the token provider is called, return our dummy JWT
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(dummyJwt);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(dummyJwt));
    }
}
