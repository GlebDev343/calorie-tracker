package com.gleb.calorietracker.dto;

import com.gleb.calorietracker.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Min(1) @Max(150)
    private int age;

    @Min(20) @Max(300)
    private double weight;

    @Min(50) @Max(250)
    private double height;

    @NotNull
    private User.Goal goal;
}