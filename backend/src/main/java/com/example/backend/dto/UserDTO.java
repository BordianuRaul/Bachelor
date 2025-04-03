package com.example.backend.dto;

import com.example.backend.model.Habit;
import com.example.backend.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> habitNames;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.habitNames = user.getHabits().stream()
                .map(Habit::getName)
                .toList();
    }

    public UserDTO() {}
}
