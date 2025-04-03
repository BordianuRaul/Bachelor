package com.example.backend.dto;

import com.example.backend.model.Habit;
import com.example.backend.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> habitNames;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.habitNames = user.getHabits().stream()
                .map(Habit::getName)
                .collect(Collectors.toList());
    }
}
