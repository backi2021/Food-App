package com.example.application;

import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.views.restaurantList.RestaurantListView;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//Quelle: https://www.youtube.com/watch?v=_rGfSKjCPb8
@SpringBootTest
public class AverageRatingTest {

    @Test
    public void calculateQualityAverage() {
        // Vorbedingung
        Restaurant restaurant = new Restaurant();

        Ordering order1 = new Ordering();
        order1.setQualityRating(5);
        order1.setRestaurantId(restaurant.getId());
        order1.setRatingSubmitted(true);

        Ordering order2 = new Ordering();
        order2.setQualityRating(3);
        order2.setRestaurantId(restaurant.getId());
        order2.setRatingSubmitted(true);

        List<Ordering> orderings = new ArrayList<>();
        orderings.add(order1);
        orderings.add(order2);

        RestaurantListView restaurantListView = Mockito.mock(RestaurantListView.class);
        Mockito.doCallRealMethod().when(restaurantListView).createMediumQualityRating(Mockito.any());

        double result = restaurantListView.createMediumQualityRating(orderings);

        assertThat(result).isEqualTo(4);
    }

    @Test
    public void calculateDeliveryAverage() {
        // Vorbedingung
        Restaurant restaurant = new Restaurant();

        Ordering order1 = new Ordering();
        order1.setDeliveryRating(3);
        order1.setRestaurantId(restaurant.getId());
        order1.setRatingSubmitted(true);

        Ordering order2 = new Ordering();
        order2.setDeliveryRating(3);
        order2.setRestaurantId(restaurant.getId());
        order2.setRatingSubmitted(true);

        List<Ordering> orderings = new ArrayList<>();
        orderings.add(order1);
        orderings.add(order2);

        RestaurantListView restaurantListView = Mockito.mock(RestaurantListView.class);
        Mockito.doCallRealMethod().when(restaurantListView).createMediumDeliveryRating(Mockito.any());

        double result = restaurantListView.createMediumDeliveryRating(orderings);

        assertThat(result).isEqualTo(3);
    }
}
