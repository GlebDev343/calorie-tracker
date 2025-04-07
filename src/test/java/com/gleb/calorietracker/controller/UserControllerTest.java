package com.gleb.calorietracker.controller;

import com.gleb.calorietracker.dto.UserDTO;
import com.gleb.calorietracker.entity.User;
import com.gleb.calorietracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setAge(30);
        userDTO.setWeight(70);
        userDTO.setHeight(175);
        userDTO.setGoal(User.Goal.MAINTENANCE);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(greaterThan(0)))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.dailyCalorieNorm").exists());

        assertTrue(userRepository.findByEmail("john@example.com").isPresent(),
                "User should be saved in the database");
        User savedUser = userRepository.findByEmail("john@example.com").get();
        assertTrue(savedUser.getDailyCalorieNorm() > 0,
                "Daily calorie norm should be calculated");
    }

    @Test
    void testCreateUserWithDuplicateEmail() throws Exception {
        User existingUser = new User();
        existingUser.setName("Jane Doe");
        existingUser.setEmail("jane@example.com");
        existingUser.setAge(25);
        existingUser.setWeight(60);
        existingUser.setHeight(165);
        existingUser.setGoal(User.Goal.WEIGHT_LOSS);
        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("jane@example.com");
        userDTO.setAge(30);
        userDTO.setWeight(70);
        userDTO.setHeight(175);
        userDTO.setGoal(User.Goal.MAINTENANCE);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("");
        userDTO.setEmail("invalid-email");
        userDTO.setAge(200);
        userDTO.setWeight(-10);
        userDTO.setHeight(0);
        userDTO.setGoal(null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value(containsString("name: must not be blank")))
                .andExpect(jsonPath("$.message").value(containsString("email: must be a well-formed email address")))
                .andExpect(jsonPath("$.message").value(containsString("age: must be less than or equal to 150")))
                .andExpect(jsonPath("$.message").value(containsString("weight: must be greater than or equal to 20")))
                .andExpect(jsonPath("$.message").value(containsString("height: must be greater than or equal to 50")))
                .andExpect(jsonPath("$.message").value(containsString("goal: must not be null")));

        assertTrue(userRepository.findAll().isEmpty(), "No user should be saved with invalid data");
    }
}