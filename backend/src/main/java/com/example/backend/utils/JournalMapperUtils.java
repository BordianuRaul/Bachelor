package com.example.backend.utils;

import com.example.backend.dto.JournalDTO;
import com.example.backend.dto.JournalPageDTO;
import com.example.backend.model.Journal;
import com.example.backend.model.JournalPage;

import java.util.stream.Collectors;

public class JournalMapperUtils {
    public static JournalDTO toDTO(Journal journal) {
        return new JournalDTO(
                journal.getId(),
                journal.getUser().getId(),
                journal.getPages().stream().map(JournalMapperUtils::toDTO).collect(Collectors.toList())
        );
    }

    public static JournalPageDTO toDTO(JournalPage page) {
        return new JournalPageDTO(
                page.getId(),
                page.getHabitName(),
                page.getBefore(),
                page.getTime(),
                page.getLocation()
        );
    }
}
