package com.gleb.calorietracker.controller;

import com.gleb.calorietracker.dto.DishDTO;
import com.gleb.calorietracker.entity.Dish;
import com.gleb.calorietracker.repository.DishRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    @Autowired
    private DishRepository dishRepository;

    @PostMapping
    public ResponseEntity<Dish> createDish(@Valid @RequestBody DishDTO dishDTO) {
        if (dishRepository.existsByNameIgnoreCase(dishDTO.getName())) {
            throw new IllegalArgumentException("Dish with name '" + dishDTO.getName() + "' already exists");
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        return ResponseEntity.ok(dishRepository.save(dish));
    }
}