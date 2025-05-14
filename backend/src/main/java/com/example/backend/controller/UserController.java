package com.example.backend.controller;

import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Habit;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        try {
            Optional<User> existingUser = userService.findUserByEmail(userDTO.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
            }
            userService.registerNewUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed." + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String rawPassword = credentials.get("password");

            Optional<User> userOptional = userService.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            User user = userOptional.get();

            if (!userService.checkPassword(user, rawPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("user", new UserDTO(user));
            response.put("token", jwtUtils.generateToken(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            String email = jwtUtils.extractEmail(token);

            Optional<User> userOptional = userService.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            User user = userOptional.get();

            if (!jwtUtils.validateToken(token, user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token is valid.");
            response.put("user", new UserDTO(user));
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserDTO>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtils.validateTokenWithoutUser(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            List<UserDTO> userDTOs = userService.getAllUsersDTO();
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/addHabit")
    public ResponseEntity<String> addHabitToUser(@RequestBody Habit habit, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        UUID userId = jwtUtils.extractUserId(token);

        if (!userService.addHabitToUser(habit, userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Habit added successfully.");
    }


    @GetMapping("/getHabits")
    public ResponseEntity<Iterable<HabitDTO>> getAllHabits(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtUtils.extractUserId(token);

            List<HabitDTO> habits = userService.getAllHabitsForUser(userId);
            return ResponseEntity.ok(habits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

