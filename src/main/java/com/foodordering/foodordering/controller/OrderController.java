package com.foodordering.foodordering.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodordering.foodordering.entity.DriverOrderRejection;
import com.foodordering.foodordering.entity.Order;
import com.foodordering.foodordering.repository.DriverOrderRejectionRepository;
import com.foodordering.foodordering.repository.OrderRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
private final OrderRepository orderRepository;
    private final DriverOrderRejectionRepository rejectionRepository;

    public OrderController(
            OrderRepository orderRepository,
            DriverOrderRejectionRepository rejectionRepository) {
        this.orderRepository = orderRepository;
        this.rejectionRepository = rejectionRepository;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setOrderStatus("PLACED");
        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
                 }

    @GetMapping("/customer/{customerId}")
    public List<Order> getCustomerOrders(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<Order> getRestaurantOrders(@PathVariable Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    @GetMapping("/driver/{driverId}")
    public List<Order> getDriverOrders(@PathVariable Long driverId) {
        return orderRepository.findByDriverId(driverId);
    }

    @GetMapping("/available-for-driver")
    public List<Order> getAllAvailableDriverOrders() {
        return orderRepository.findByOrderStatusAndDriverIdIsNull("READY_FOR_PICKUP");
    }

    @GetMapping("/available-for-driver/{driverId}")
    public List<Order> getAvailableOrdersForDriver(@PathVariable Long driverId) {
        Set<Long> rejectedOrderIds = rejectionRepository.findByDriverId(driverId)
        .stream()
                .map(DriverOrderRejection::getOrderId)
                .collect(Collectors.toSet());

        return orderRepository.findByOrderStatusAndDriverIdIsNull("READY_FOR_PICKUP")
                .stream()
                .filter(order -> !rejectedOrderIds.contains(order.getId()))
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/timing")
    public Order updateOrderTiming(@PathVariable Long id, @RequestBody Order timing) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (timing.getPreparationTimeMinutes() == null
                || timing.getPreparationTimeMinutes() <= 0) {
            throw new RuntimeException("Preparation time must be greater than zero");
        }
        if (timing.getExpectedDeliveryTime() == null
                || timing.getExpectedDeliveryTime().isBlank()) {
            throw new RuntimeException("Expected delivery time is required");
        }

        order.setPreparationTimeMinutes(timing.getPreparationTimeMinutes());
         order.setExpectedDeliveryTime(timing.getExpectedDeliveryTime());
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/accept")
    public Order acceptOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("RESTAURANT_ACCEPTED");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/reject")
    public Order rejectOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("RESTAURANT_REJECTED");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/preparing")
    public Order prepareOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("PREPARING");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/ready")
    public Order readyForPickup(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("READY_FOR_PICKUP");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/driver-accept/{driverId}")
    public Order driverAcceptOrder(@PathVariable Long id, @PathVariable Long driverId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!"READY_FOR_PICKUP".equals(order.getOrderStatus())) {
            throw new RuntimeException("Order is not available for driver pickup");
        }
        if (order.getDriverId() != null) {
            throw new RuntimeException("Order has already been assigned to a driver");
        }
        order.setDriverId(driverId);
        order.setOrderStatus("DRIVER_ACCEPTED");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/driver-reject/{driverId}")
    public Order driverRejectOrder(@PathVariable Long id, @PathVariable Long driverId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!"READY_FOR_PICKUP".equals(order.getOrderStatus()) || order.getDriverId() != null) {
            throw new RuntimeException("This order is no longer available to decline");
        }

        if (rejectionRepository.findByOrderIdAndDriverId(id, driverId).isEmpty()) {
            rejectionRepository.save(new DriverOrderRejection(id, driverId));
        }
        return order;
    }

    @PutMapping("/{id}/picked-up")
    public Order pickedUpOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("PICKED_UP");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/out-for-delivery")
    public Order outForDelivery(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("OUT_FOR_DELIVERY");
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/delivered")
    public Order deliveredOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("DELIVERED");
        return orderRepository.save(order);
    }
}
