
package com.example.application.data.repository;

import com.example.application.data.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    List<Favourite> findAllByCustomerId(Integer serId);

    List<Favourite> findAll();


}
