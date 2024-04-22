package com.example.application.data.repository.userRepository;

import com.example.application.data.entity.user.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Integer> {

    RestaurantOwner getByEmail(String email);

    Boolean existsByEmail(String email);

   // User getByFirstName();

    //User getByLastName();

   // User getById();

    RestaurantOwner getByActivationCode(String activationCode);




}
