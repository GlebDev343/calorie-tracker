package com.gleb.calorietracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MealDTO {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<Long> dishIds;

    @NotNull
    private LocalDateTime dateTime;
}