package com.example.backend.controller;

import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Habit;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
            }
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            Optional<User> userOptional = userService.findUserByEmail(credentials.get("email"));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            User user = userOptional.get();
            if (user.getPassword().equals(credentials.get("password"))) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful.");
                response.put("user", new UserDTO(user));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> userDTOs = userService.getAllUsersDTO();
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //ADD JWT FOR TOKENIZED LOGIN
    @PostMapping("/addHabit")
    public ResponseEntity<String> addHabitToUser(@RequestBody Habit habit, UUID userId) {
        if (!userService.addHabitToUser(habit, userId))
            return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.CREATED).body("Habit added successfully.");
    }

    @GetMapping("/getHabits")
    public ResponseEntity<Iterable<HabitDTO>> getAllHabits(UUID userId) {
        try {
            return ResponseEntity.ok(userService.getAllHabitsForUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

