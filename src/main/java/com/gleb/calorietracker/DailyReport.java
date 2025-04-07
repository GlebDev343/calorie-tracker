package com.gleb.calorietracker;

import com.gleb.calorietracker.entity.Meal;
import lombok.Data;

import java.util.List;

@Data
public class DailyReport {
    private double totalCalories;
    private double dailyNorm;
    private List<Meal> meals;
    private boolean withinNorm;

    public DailyReport(double totalCalories, double dailyNorm, List<Meal> meals, boolean withinNorm) {
        this.totalCalories = totalCalories;
        this.dailyNorm = dailyNorm;
        this.meals = meals;
        this.withinNorm = withinNorm;
    }
}