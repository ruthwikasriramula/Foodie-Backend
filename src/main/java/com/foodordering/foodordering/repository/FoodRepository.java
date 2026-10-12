package com.foodordering.foodordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodordering.foodordering.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
}