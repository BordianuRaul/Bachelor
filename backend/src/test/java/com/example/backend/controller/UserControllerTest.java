package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_NewUser_ReturnsCreated() {
        User user = new User("John", "Doe", "john@example.com", "password");
        UserDTO userDTO = new UserDTO(user);

        when(userService.findUserByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.register(userDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registered successfully.", response.getBody());
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ReturnsConflict() {
        User user = new User("John", "Doe", "john@example.com", "password");
        UserDTO userDTO = new UserDTO(user);
        when(userService.findUserByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = userController.register(userDTO);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User already exists.", response.getBody());
        verify(userService, never()).save(any());
    }

    @Test
    void register_ExceptionThrown_ReturnsServerError() {
        User user = new User("John", "Doe", "john@example.com", "password");
        UserDTO userDTO = new UserDTO(user);
        when(userService.findUserByEmail(userDTO.getEmail())).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<String> response = userController.register(userDTO);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Registration failed."));
    }

    @Test
    void login_Successful_ReturnsUserDTO() {
        User user = new User("Alice", "Wonder", "alice@example.com", "secret");
        Map<String, String> credentials = Map.of("email", "alice@example.com", "password", "secret");

        when(userService.findUserByEmail("alice@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = userController.login(credentials);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful.", response.getBody().get("message"));
        assertNotNull(response.getBody().get("user"));
    }

    @Test
    void login_WrongPassword_ReturnsUnauthorized() {
        User user = new User("Bob", "Builder", "bob@example.com", "password123");
        Map<String, String> credentials = Map.of("email", "bob@example.com", "password", "wrongpass");

        when(userService.findUserByEmail("bob@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = userController.login(credentials);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void login_UserNotFound_ReturnsNotFound() {
        Map<String, String> credentials = Map.of("email", "ghost@example.com", "password", "nopass");

        when(userService.findUserByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = userController.login(credentials);

        assertEquals(404, response.getStatusCodeValue());
    }
}
