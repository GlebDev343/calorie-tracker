package com.gleb.calorietracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DishDTO {
    @NotBlank
    private String name;

    @Min(0)
    private double calories;

    @Min(0)
    private double proteins;

    @Min(0)
    private double fats;

    @Min(0)
    private double carbs;
}