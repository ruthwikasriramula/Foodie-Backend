package com.foodordering.foodordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodordering.foodordering.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}