package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "locationTimestamps")
public class LocationTimestamp {

    @Id
    private final String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime dateTime;

    private double longitude;

    private double latitude;

    private String locationName;
}
