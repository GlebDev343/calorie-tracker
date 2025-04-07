package com.gleb.calorietracker.controller;

import com.gleb.calorietracker.DailyReport;
import com.gleb.calorietracker.dto.MealDTO;
import com.gleb.calorietracker.entity.Dish;
import com.gleb.calorietracker.entity.Meal;
import com.gleb.calorietracker.entity.User;
import com.gleb.calorietracker.exception.ResourceNotFoundException;
import com.gleb.calorietracker.repository.DishRepository;
import com.gleb.calorietracker.repository.MealRepository;
import com.gleb.calorietracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishRepository dishRepository;

    @PostMapping
    public ResponseEntity<Meal> addMeal(@Valid @RequestBody MealDTO mealDTO) {
        User user = userRepository.findById(mealDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Dish> dishes = dishRepository.findAllByIdIn(mealDTO.getDishIds());
        if (dishes.size() != mealDTO.getDishIds().size()) {
            throw new ResourceNotFoundException("Some dishes not found");
        }

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setDishes(dishes);
        meal.setDateTime(mealDTO.getDateTime());

        return ResponseEntity.ok(mealRepository.save(meal));
    }

    @GetMapping("/daily/{userId}")
    public ResponseEntity<DailyReport> getDailyReport(@PathVariable Long userId,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Meal> meals = mealRepository.findByUserAndDateTimeBetween(user, start, end);

        double totalCalories = meals.stream()
                .flatMap(m -> m.getDishes().stream())
                .mapToDouble(Dish::getCalories)
                .sum();

        return ResponseEntity.ok(new DailyReport(
                totalCalories,
                user.getDailyCalorieNorm(),
                meals,
                totalCalories <= user.getDailyCalorieNorm()
        ));
    }
}