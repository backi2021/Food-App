package com.example.application.data.repository.customerRepository;

import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
   /* @Query("select r from Customer r " + "where lower(r.firstName) like lower(concat('%', :searchTerm, '%'))")
    List<Customer> search(@Param("searchTerm") String searchTerm);*/

    Customer getByEmail(String email);


    Boolean existsByEmail(String email);

    Customer getById(Integer id); //getByid reicht das aus für den aktuellen User

    Customer getByActivationCode(String activationCode);

    List<Customer>findAll();
}
