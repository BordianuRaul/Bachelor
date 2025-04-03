package com.example.backend.service;

import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Habit;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public boolean addHabitToUser(Habit habit, UUID userId) {
        try {
            User user = findUserById(userId);
            habit.setUser(user);
            user.getHabits().add(habit);
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    private User findUserById(final UUID userId) {
        return userRepository.findUserById(userId);
    }

    public List<HabitDTO> getAllHabitsForUser(final UUID userId) {
        return findUserById(userId).getHabits().stream().map(HabitDTO::new).toList();
    }

    public List<UserDTO> getAllUsersDTO() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDTO::new).toList();
    }
}

