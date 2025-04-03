package com.example.backend.dto;

import com.example.backend.model.Habit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabitDTO {
    private String id;
    private String name;
    public HabitDTO(Habit habit) {
        this.id = habit.getId();
        this.name = habit.getName();
    }
}
