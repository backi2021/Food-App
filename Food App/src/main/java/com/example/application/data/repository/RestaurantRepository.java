package com.example.application.data.repository;

import com.example.application.data.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    @Query("select r from Restaurant r " + "where lower(r.restaurant_name) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(r.restaurant_category) like lower(concat('%', :searchTerm, '%'))")
    List<Restaurant> search(@Param("searchTerm") String searchTerm);

    @Query("select r from Restaurant r  where r.restaurantOwners_Id = ?1")
    Restaurant findFirstByUser(Integer user);


    List<Restaurant> findAll();

    Restaurant findFirstById(Integer id);

    ArrayList<Restaurant> findRestaurantById(Integer Id);


}
