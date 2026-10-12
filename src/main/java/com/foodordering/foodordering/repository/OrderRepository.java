package com.foodordering.foodordering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodordering.foodordering.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByDriverId(Long driverId);
    List<Order> findByOrderStatusAndDriverIdIsNull(String orderStatus);
}
