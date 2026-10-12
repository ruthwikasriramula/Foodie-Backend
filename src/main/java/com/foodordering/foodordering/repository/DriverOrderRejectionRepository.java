package com.foodordering.foodordering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodordering.foodordering.entity.DriverOrderRejection;

public interface DriverOrderRejectionRepository extends JpaRepository<DriverOrderRejection, Long> {
    List<DriverOrderRejection> findByDriverId(Long driverId);
    Optional<DriverOrderRejection> findByOrderIdAndDriverId(Long orderId, Long driverId);
}
