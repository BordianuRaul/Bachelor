package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "applicationHourlyUsages")
public class ApplicationHourlyUsage {
    @Id
    private final String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int hour;

    private int secondsSpent;

    private LocalDate date;

    public ApplicationHourlyUsage(User user, Application app, int hour, int secondsSpent, LocalDate date) {
        this.user = user;
        this.application = app;
        this.hour = hour;
        this.secondsSpent = secondsSpent;
        this.date = date;
    }

    public ApplicationHourlyUsage() {

    }
}
