package com.example.backend.repository;

import com.example.backend.model.Journal;
import com.example.backend.model.JournalPage;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
}
