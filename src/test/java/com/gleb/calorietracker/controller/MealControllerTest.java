package com.gleb.calorietracker.controller;

import com.gleb.calorietracker.dto.MealDTO;
import com.gleb.calorietracker.entity.Dish;
import com.gleb.calorietracker.entity.Meal;
import com.gleb.calorietracker.entity.User;
import com.gleb.calorietracker.repository.DishRepository;
import com.gleb.calorietracker.repository.MealRepository;
import com.gleb.calorietracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Dish testDish;

    @BeforeEach
    void setUp() {
        mealRepository.deleteAll();
        userRepository.deleteAll();
        dishRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setAge(25);
        testUser.setWeight(70);
        testUser.setHeight(175);
        testUser.setGoal(User.Goal.MAINTENANCE);
        testUser.setDailyCalorieNorm(1800);
        userRepository.save(testUser);

        testDish = new Dish();
        testDish.setName("Test Dish");
        testDish.setCalories(300);
        testDish.setProteins(20);
        testDish.setFats(10);
        testDish.setCarbs(30);
        dishRepository.save(testDish);
    }

    @Test
    void testAddMealSuccess() throws Exception {
        MealDTO mealDTO = new MealDTO();
        mealDTO.setUserId(testUser.getId());
        mealDTO.setDishIds(List.of(testDish.getId()));
        mealDTO.setDateTime(LocalDateTime.now());

        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mealDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(testUser.getId()))
                .andExpect(jsonPath("$.dishes[0].id").value(testDish.getId()));
    }

    @Test
    void testGetDailyReport() throws Exception {
        Meal meal = new Meal();
        meal.setUser(testUser);
        meal.setDishes(List.of(testDish));
        meal.setDateTime(LocalDateTime.now());
        mealRepository.save(meal);

        mockMvc.perform(get("/api/meals/daily/" + testUser.getId())
                        .param("date", LocalDateTime.now().toLocalDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(300.0))
                .andExpect(jsonPath("$.dailyNorm").value(1800.0))
                .andExpect(jsonPath("$.withinNorm").value(true));
    }

    @Test
    void testAddMealWithNonExistentUser() throws Exception {
        MealDTO mealDTO = new MealDTO();
        mealDTO.setUserId(999L);
        mealDTO.setDishIds(List.of(testDish.getId()));
        mealDTO.setDateTime(LocalDateTime.now());

        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mealDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }
}