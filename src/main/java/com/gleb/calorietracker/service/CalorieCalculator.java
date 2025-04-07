package com.gleb.calorietracker.service;

import com.gleb.calorietracker.entity.User;
import org.springframework.stereotype.Service;

@Service
public class CalorieCalculator {
    public double calculateDailyNorm(User user) {
        double bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge();
        bmr += (user.getGoal() == User.Goal.WEIGHT_LOSS ? -161 : 5);

        switch (user.getGoal()) {
            case WEIGHT_LOSS: return bmr * 0.85;
            case MAINTENANCE: return bmr;
            case MUSCLE_GAIN: return bmr * 1.15;
            default: return bmr;
        }
    }
}