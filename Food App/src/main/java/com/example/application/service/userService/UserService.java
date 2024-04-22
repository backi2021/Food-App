package com.example.application.service.userService;

import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import com.example.application.data.repository.userRepository.RestaurantOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {



    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private CustomerRepository customerRepository; //final?

    @Autowired
    public UserService(RestaurantOwnerRepository restaurantOwnerRepository) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }

    /*public UserService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }*/

    public void createProfile(RestaurantOwner restaurantOwnerProfile) {
        this.restaurantOwnerRepository.save(restaurantOwnerProfile);

    }
    /*public void createProfile(Customer customerProfile) {
        this.customerRepository.save(customerProfile);
    }*/

    public boolean emailExists(String email){
        return restaurantOwnerRepository.existsByEmail(email);
    }


    public void delete (RestaurantOwner restaurantOwnerProfile){
        restaurantOwnerRepository.delete(restaurantOwnerProfile);
    }

    /*public void delete (Customer customerProfile){
        customerRepository.delete(customerProfile);
    }*/


}
