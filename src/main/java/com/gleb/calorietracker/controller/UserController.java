package com.gleb.calorietracker.controller;

import com.gleb.calorietracker.dto.UserDTO;
import com.gleb.calorietracker.entity.User;
import com.gleb.calorietracker.repository.UserRepository;
import com.gleb.calorietracker.service.CalorieCalculator;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalorieCalculator calculator;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setDailyCalorieNorm(calculator.calculateDailyNorm(user));

        return ResponseEntity.ok(userRepository.save(user));
    }
}