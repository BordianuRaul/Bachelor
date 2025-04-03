package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journals")
@Getter
public class Journal {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalPage> pages;

    public Journal() {
        this.pages = new ArrayList<>();
    }

    public Journal(User user) {
        this.user = user;
        this.pages = new ArrayList<>();
    }
}
