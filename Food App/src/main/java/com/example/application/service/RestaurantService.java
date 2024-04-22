package com.example.application.service;

import com.example.application.data.entity.Favourite;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.repository.FavouriteRepository;
import com.example.application.data.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final FavouriteRepository favouriteRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, FavouriteRepository favouriteRepository) {
        this.restaurantRepository = restaurantRepository;
        this.favouriteRepository = favouriteRepository;
    }

    public Restaurant findRestaurant(Integer userId) {
        return restaurantRepository.findFirstByUser(userId);
    }


    public List<Restaurant> findAllRestaurants(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return restaurantRepository.findAll();
        } else {
            return restaurantRepository.search(stringFilter);
        }
    }

    public List<Restaurant> findAll(){
        return restaurantRepository.findAll();
    };

    public void deleteRestaurant(Restaurant restaurant) {
        restaurantRepository.delete(restaurant);
    }

    public void saveRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            System.err.println("Restaurant is NULL");
            return;
        }
        restaurantRepository.save(restaurant);
    }



    public Restaurant findById (Integer restaurantId) {
        return restaurantRepository.findFirstById(restaurantId);

    }


    public List<Restaurant> findFavouritesOfUser(Integer userId){
        List<Restaurant> favourites = new ArrayList<>();
        for (Favourite f : favouriteRepository.findAllByCustomerId(userId)) {
            favourites.add(restaurantRepository.findFirstById(f.getRestaurantId()));
        }
        return favourites;
    }

    // Quelle: Haversine formula
    public List<Restaurant> findByDistance(double lat1, double lng1, double lat2, double lng2){
        List<Restaurant> tempArray = restaurantRepository.findAll();
        List<Restaurant> allRestaurantsByDistance = new ArrayList<>();

        for (Restaurant r : tempArray) {
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = (double) (earthRadius * c);

            if(dist <= r.getRestaurant_radius()*1000){
                allRestaurantsByDistance.add(restaurantRepository.getById(r.getRestaurantId()));
            }
        }
        return allRestaurantsByDistance;
    }

    // Quelle: Haversine formula
    public Double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);
        return dist;
    }


    public List<Restaurant> findAllRestaurants(Integer id) {
        List<Restaurant> allRestaruants = new ArrayList<>();
        for(Restaurant r : restaurantRepository.findRestaurantById(id)){
            allRestaruants.add(restaurantRepository.getById(r.getRestaurantId()));
        }
        return allRestaruants;
    }



}
