package com.example.backend.dto;

import java.util.List;
import java.util.UUID;

public record JournalDTO(
        UUID id,
        UUID userId,
        List<JournalPageDTO> pages
) {}
