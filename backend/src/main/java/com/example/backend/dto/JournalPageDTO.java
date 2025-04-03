package com.example.backend.dto;

import java.util.UUID;

public record JournalPageDTO(
        UUID id,
        String habitName,
        String before,
        String time,
        String location
) {}
