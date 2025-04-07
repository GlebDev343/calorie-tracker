package com.gleb.calorietracker.service;

import com.gleb.calorietracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CalorieCalculatorTest {

    @Autowired
    private CalorieCalculator calculator;

    @Test
    void testCalculateDailyNormForMaintenance() {
        User user = new User();
        user.setWeight(70);
        user.setHeight(175);
        user.setAge(30);
        user.setGoal(User.Goal.MAINTENANCE);

        double norm = calculator.calculateDailyNorm(user);
        assertTrue(norm > 1500 && norm < 2000, "Daily norm should be between 1500 and 2000 calories");
    }

    @Test
    void testCalculateDailyNormForWeightLoss() {
        User user = new User();
        user.setWeight(70);
        user.setHeight(175);
        user.setAge(30);
        user.setGoal(User.Goal.WEIGHT_LOSS);

        double norm = calculator.calculateDailyNorm(user);
        assertTrue(norm > 1200 && norm < 1700, "Daily norm for weight loss should be lower than maintenance");
    }
}