package com.example.backend.controller;

import com.example.backend.service.SRHIService;
import com.example.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/SRHI")
public class SRHIController {
    private final JwtUtils jwtUtils;

    @Autowired
    private final SRHIService srhiService;

    public SRHIController(JwtUtils jwtUtils, final SRHIService srhiService) {
        this.jwtUtils = jwtUtils;
        this.srhiService = srhiService;
    }

    @PostMapping("/result")
    public ResponseEntity<String> getInformationAboutTheHabitsSRHI(@RequestHeader("Authorization") String authHeader, @RequestBody List<Integer> answers) {
        try {
            String token = authHeader.substring("Bearer ".length());

            if (!jwtUtils.validateTokenWithoutUser(token)) {
                return ResponseEntity.status(403).body("Invalid or expired token.");
            }

            String result = srhiService.getInformationAboutTheHabitsSRHI(answers);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
