package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "journal_page")
@Getter
@Setter
public class JournalPage {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String habitName;
    @Column(name = "`before`")
    private String before;
    private String time;
    private String location;

    @ManyToOne
    @JoinColumn(name= "journal_id", nullable = false)
    private Journal journal;

    public JournalPage() {

    }

    public JournalPage(String habitName, String before, String time, String location, Journal journal) {
        this.habitName = habitName;
        this.before = before;
        this.time = time;
        this.location = location;
        this.journal = journal;
    }
}
