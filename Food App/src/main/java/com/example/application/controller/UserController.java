package com.example.application.controller;

import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/user")
@ResponseStatus(HttpStatus.ACCEPTED)
public class UserController {

    private final UserService userService;

    /*
    NUR FÜR DATENBANK ERSTELLT , DAMIT MAN TESTEN KANN !
     */

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public void register(@RequestBody RestaurantOwner restaurantOwnerProfile){userService.createProfile(restaurantOwnerProfile);}

//    @PostMapping()
//    public void register(@RequestBody Customer customerProfile){userService.createProfile(customerProfile);}

    @GetMapping("/exists/{email}")
    public boolean existsByEmail(@PathVariable String email){return userService.emailExists(email);}

    @DeleteMapping("/delete/{user}")
    public void delete(RestaurantOwner restaurantOwnerProfile){userService.delete(restaurantOwnerProfile);}

//    @DeleteMapping("/delete/{user}")
//    public void delete(Customer customerProfile){userService.delete(customerProfile);}


}
