package com.example.application.data.repository;

import com.example.application.data.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    @Query("select m from Meal m " +
            "where lower(m.mealName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(m.mealDetails) like lower(concat('%', :searchTerm, '%'))")
    List<Meal> search(@Param("searchTerm") String searchTerm);

    @Query("select m from Meal m  where m.restaurantId = ?1")
    List<Meal> findAllFromRestaurant(Integer rId);

    List<Meal> findByRestaurantId(Integer restaurantId);

    @Query("select m from Meal m " +
            "where lower(m.mealName) like lower(concat('%', :searchTerm, '%'))")
    List<Meal> searchAllByRestaurantId(@Param("searchTerm") String searchTerm);


    //_________________________________________________
    /*List<Meal> findById (List<OrderItem> orderItems);*/

    List<Meal> findAllById (Integer mealId);

    Meal findFirstById(Integer mealId);

}
