package com.example.coffeeshop.service;

import com.example.coffeeshop.model.User;
import com.example.coffeeshop.repository.UserRepository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // --- ARRANGE ---
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setRoles("ROLE_USER");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // --- ACT ---
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // --- ASSERT ---
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("testuser", userDetails.getUsername());
        Assertions.assertEquals("hashedpassword", userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        // --- ARRANGE ---
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Verify that a UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });
    }
}
