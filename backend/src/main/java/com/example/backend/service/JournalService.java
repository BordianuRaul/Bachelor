package com.example.backend.service;

import com.example.backend.model.Journal;
import com.example.backend.model.JournalPage;
import com.example.backend.repository.JournalPageRepository;
import com.example.backend.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class JournalService {
    private final JournalRepository journalRepository;
    private final JournalPageRepository journalPageRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository, JournalPageRepository journalPageRepository) {
        this.journalRepository = journalRepository;
        this.journalPageRepository = journalPageRepository;
    }

    public void addJournalPageToJournal(JournalPage journalPage, UUID userId) {
        final Optional<Journal> journalOptional = journalRepository.findById(userId);

        if (journalOptional.isPresent()) {
            final Journal journal = journalOptional.get();
            journalPage.setJournal(journal);

            journalPageRepository.save(journalPage);
        } else {
            throw new RuntimeException("Journal not found for user");
        }
    }
}
