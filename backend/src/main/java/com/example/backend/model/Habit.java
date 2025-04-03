package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "habits")
public class Habit {
    @Id
    private final String id = UUID.randomUUID().toString();

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Habit() {

    }
    public Habit(String name) {
        this.name = name;
    }
}
