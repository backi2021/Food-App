package com.example.application.data.repository;

import com.example.application.data.entity.Meal;
import com.example.application.data.entity.OrderItem;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Ordering, Integer> {
    //List<Ordering> search(@Param("searchTerm") String searchTerm);

    List<Ordering> findByCustomerId(Integer customerId);

    //ZUM TESTEN
    Ordering findFirstByCustomerId(Integer customerId);

    Ordering findFirstById(Integer orderId);

    List<Ordering> findByRestaurantId(Integer restaurantId);

    //List<Ordering> findAll();
}
