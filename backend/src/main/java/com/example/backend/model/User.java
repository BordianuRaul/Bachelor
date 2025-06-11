package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private String firstName;

    @Setter
    private String lastName;

    @Setter
    private String email;

    @Setter
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habit> habits;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Journal journal;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationTimestamp> locationTimestamps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationHourlyUsage> applicationHourlyUsages;


    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.habits = new ArrayList<>();
        this.journal = new Journal(this);
        this.locationTimestamps = new ArrayList<>();
        this.applicationHourlyUsages = new ArrayList<>();
    }

    public User() {
        this.habits = new ArrayList<>();
        this.journal = new Journal(this);
    }
}
