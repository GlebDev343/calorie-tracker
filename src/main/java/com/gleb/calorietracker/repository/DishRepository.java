package com.gleb.calorietracker.repository;

import com.gleb.calorietracker.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Dish> findAllByIdIn(List<Long> ids);
}