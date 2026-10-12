package com.foodordering.foodordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodordering.foodordering.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}