package com.example.backend.controller;

import com.example.backend.dto.ApplicationDailyUsageDTO;
import com.example.backend.model.User;
import com.example.backend.service.AppUsageService;
import com.example.backend.service.ApplicationService;
import com.example.backend.service.UserService;
import com.example.backend.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/appUsage")
public class AppUsageController {

    private final AppUsageService applicationHourlyUsageService;

    private final ApplicationService applicationService;

    private final UserService userService;

    private final JwtUtils jwtUtils;


    public AppUsageController(AppUsageService applicationHourlyUsageService, JwtUtils jwtUtils, ApplicationService applicationService, UserService userService) {
        this.applicationHourlyUsageService = applicationHourlyUsageService;
        this.jwtUtils = jwtUtils;
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/lastUpdate")
    public ResponseEntity<String> getDateOfTheLastUpdate(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);
            UUID userId = jwtUtils.extractUserId(token);

            LocalDate lastDate = applicationHourlyUsageService.getLastUpdateDateForUser(userId);
            return ResponseEntity.ok(lastDate.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve last update: " + e.getMessage());
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<String> updateApplicationUsageInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<ApplicationDailyUsageDTO> usageList) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);
            UUID userId = jwtUtils.extractUserId(token);
            User user = userService.findUserById(userId);

            applicationService.updateApplicationsUsed(usageList);
            applicationHourlyUsageService.updateAppUsage(user, usageList);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Application usage info updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed updating apps usage info. " + e.getMessage());
        }
    }

    @GetMapping("/findPossibleHabits")
    public ResponseEntity<List<String>> findPossibleHabits(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(List.of("Missing or invalid Authorization header."));
            }

            String token = authHeader.substring(7);
            UUID userId = jwtUtils.extractUserId(token);
            User user = userService.findUserById(userId);

            List<String> habitMessages = applicationHourlyUsageService.findPossibleHabits(user);
            return ResponseEntity.ok(habitMessages);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Failed to retrieve possible habits: " + e.getMessage()));
        }
    }
}
