package com.example.backend.controller;

import com.example.backend.dto.JournalPageDTO;
import com.example.backend.model.JournalPage;
import com.example.backend.service.JournalService;
import com.example.backend.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JournalControllerTest {

    @InjectMocks
    private JournalController journalController;

    @Mock
    private JournalService journalService;

    @Mock
    private JwtUtils jwtUtils;

    private AutoCloseable closeable;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPageToJournal_ValidToken_ReturnsOk() {
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;

        JournalPageDTO dto = new JournalPageDTO(
                UUID.randomUUID(),
                "Exercise",
                "Woke up and drank water",
                "08:00 AM",
                "Home"
        );


        when(jwtUtils.validateTokenWithoutUser(token)).thenReturn(true);
        when(jwtUtils.extractUserId(token)).thenReturn(userId);

        ResponseEntity<String> response = journalController.addPageToJournal(dto, authHeader);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Journal page added successfully.", response.getBody());
        verify(journalService, times(1)).addJournalPageToJournal(any(JournalPage.class), eq(userId));
    }

    @Test
    void addPageToJournal_InvalidToken_ReturnsForbidden() {
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;

        JournalPageDTO dto = new JournalPageDTO(
                UUID.randomUUID(),
                "Exercise",
                "Woke up and drank water",
                "08:00 AM",
                "Home"
        );


        when(jwtUtils.validateTokenWithoutUser(token)).thenReturn(false);

        ResponseEntity<String> response = journalController.addPageToJournal(dto, authHeader);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Invalid or expired token.", response.getBody());
        verify(journalService, never()).addJournalPageToJournal(any(), any());
    }

    @Test
    void addPageToJournal_ServiceThrowsException_ReturnsServerError() {
        String token = "valid.token";
        String authHeader = "Bearer " + token;

        JournalPageDTO dto = new JournalPageDTO(
                UUID.randomUUID(),
                "Exercise",
                "Woke up and drank water",
                "08:00 AM",
                "Home"
        );
        when(jwtUtils.validateTokenWithoutUser(token)).thenReturn(true);
        when(jwtUtils.extractUserId(token)).thenReturn(userId);
        doThrow(new RuntimeException("DB failure")).when(journalService).addJournalPageToJournal(any(), eq(userId));


        ResponseEntity<String> response = journalController.addPageToJournal(dto, authHeader);


        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Error adding journal page:"));
    }
}
