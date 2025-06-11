package com.example.backend.controller;

import com.example.backend.dto.ApplicationDailyUsageDTO;
import com.example.backend.model.User;
import com.example.backend.service.ApplicationHourlyUsageService;
import com.example.backend.service.ApplicationService;
import com.example.backend.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/appUsage")
public class AppUsageController {

    private final ApplicationHourlyUsageService applicationHourlyUsageService;

    private final JwtUtils jwtUtils;

    private final ApplicationService applicationService;

    public AppUsageController(ApplicationHourlyUsageService applicationHourlyUsageService, JwtUtils jwtUtils, ApplicationService applicationService) {
        this.applicationHourlyUsageService = applicationHourlyUsageService;
        this.jwtUtils = jwtUtils;
        this.applicationService = applicationService;
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

        // 🔍 Set breakpoint here to inspect incoming list in debugger
        System.out.println("Received usage data: " + usageList.size());

        // Example print for quick CLI check (can be removed)
        for (ApplicationDailyUsageDTO usage : usageList) {
            System.out.println("App: " + usage.name + ", Package: " + usage.packageName);
        }

        return ResponseEntity.ok("Received " + usageList.size() + " records");
    }



}
