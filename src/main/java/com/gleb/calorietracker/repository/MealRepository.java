package com.gleb.calorietracker.repository;

import com.gleb.calorietracker.entity.Meal;
import com.gleb.calorietracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("SELECT m FROM Meal m JOIN FETCH m.dishes WHERE m.user = :user AND m.dateTime BETWEEN :start AND :end")
    List<Meal> findByUserAndDateTimeBetween(@Param("user") User user,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}