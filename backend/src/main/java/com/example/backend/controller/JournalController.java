package com.example.backend.controller;

import com.example.backend.dto.JournalPageDTO;
import com.example.backend.model.JournalPage;
import com.example.backend.service.JournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/journal")
public class JournalController {
    private final JournalService journalService;

    JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping("addPageToJournal")
    public ResponseEntity<String> addPageToJournal(@RequestBody JournalPageDTO journalPageDTO, @RequestParam UUID userId) {
        try {
            final JournalPage journalPage = new JournalPage(
                    journalPageDTO.habitName(),
                    journalPageDTO.before(),
                    journalPageDTO.time(),
                    journalPageDTO.location(),
                    null
            );

            journalService.addJournalPageToJournal(journalPage, userId);
            return ResponseEntity.ok("Journal page added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding journal page: " + e.getMessage());
        }
    }
}
