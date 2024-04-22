package com.example.application;

import com.example.application.data.entity.Meal;
import com.example.application.data.entity.OrderStatus;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.repository.OrderRepository;
import com.example.application.service.OrderService;
import com.example.application.views.ordering.BasketLayout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasketLayoutTest { // Quelle: Vaadin Youtube Tutorials

    @Autowired
    private OrderService orderService;

    Ordering order = new Ordering();

    Ordering order1 = new Ordering();

    Ordering order2 = new Ordering();

    Ordering order3 = new Ordering();






    @Test
    public void shouldAddMealsTotal(){

        // given
        Meal meal1 = new Meal();
        meal1.setMealPrice(10.4);
        Meal meal2 = new Meal();
        meal2.setMealPrice(4.6);

        List<Meal> meals = new ArrayList<>();
        meals.add(meal1);
        meals.add(meal2);

        // when
        BasketLayout basketLayout = Mockito.mock(BasketLayout.class);
        Mockito.doCallRealMethod().when(basketLayout).getMealsTotal();
        Mockito.doCallRealMethod().when(basketLayout).setMeals(Mockito.anyList());
        basketLayout.setMeals(meals);

        Double result = basketLayout.getMealsTotal();

        // then
        assertThat(result).isEqualTo(15);



    }

    @Test
    public void shouldAddCompleteTotal(){

        // given
        Meal meal1 = new Meal();
        meal1.setMealPrice(22.0);
        Meal meal2 = new Meal();
        meal2.setMealPrice(1.3);

        List<Meal> meals = new ArrayList<>();
        meals.add(meal1);
        meals.add(meal2);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurant_delivery_price(2.3);

        // when
        BasketLayout basketLayout = Mockito.mock(BasketLayout.class);
        Mockito.doCallRealMethod().when(basketLayout).getMealsTotal();
        Mockito.doCallRealMethod().when(basketLayout).setMeals(Mockito.anyList());
        Mockito.doCallRealMethod().when(basketLayout).setRestaurant(Mockito.any());
        Mockito.doCallRealMethod().when(basketLayout).getTotal();

        basketLayout.setRestaurant(restaurant);
        basketLayout.setMeals(meals);

        Double result = basketLayout.getTotal();

        // then
        assertThat(result).isEqualTo(25.6);


    }

    @Test
    public void shouldSaveOrder(){
        order.setCustomerId(999);
        order.setCustomerId(999);
        order.setDestinationCity("Duisburg");
        order.setDestinationZip(47443);
        order.setDestinationStreet("Mainstraße 25");
        order.setOrderTotal(99.99);
        order.setOrderTime(LocalDateTime.now());

        Meal meal1 = new Meal();
        meal1.setMealPrice(22.0);
        Meal meal2 = new Meal();
        meal2.setMealPrice(1.3);


        List<Meal> meals = new ArrayList<>();
        meals.add(meal1);
        meals.add(meal2);


        orderService.saveOrder(order, meals);

        assertThat(order).isEqualTo(orderService.findByOrderId(order.getId()));



    }


    @Test
    public  void shouldCalculateDeliveryTime(){
        order1.setCustomerId(999);
        order1.setRestaurantId(999);
        order1.setStatus(OrderStatus.ACCEPTED);
        order1.setDestinationCity("Duisburg");
        order1.setDestinationZip(47443);
        order1.setDestinationStreet("Mainstraße 25");
        order1.setOrderTotal(11.11);
        order1.setOrderTime(LocalDateTime.now());

        Meal meal1 = new Meal();
        meal1.setMealPrice(22.0);
        Meal meal2 = new Meal();
        meal2.setMealPrice(1.3);


        List<Meal> meals = new ArrayList<>();
        meals.add(meal1);
        meals.add(meal2);

        orderService.saveOrder(order1, meals);

        order2.setCustomerId(999);
        order2.setRestaurantId(999);
        order2.setStatus(OrderStatus.ACCEPTED);
        order2.setDestinationCity("Duisburg");
        order2.setDestinationZip(47443);
        order2.setDestinationStreet("Mainstraße 25");
        order2.setOrderTotal(22.22);
        order2.setOrderTime(LocalDateTime.now());


        orderService.saveOrder(order2, meals);

        order3.setCustomerId(999);
        order3.setRestaurantId(999);
        order3.setStatus(OrderStatus.ACCEPTED);
        order3.setDestinationCity("Duisburg");
        order3.setDestinationZip(47443);
        order3.setDestinationStreet("Mainstraße 25");
        order3.setOrderTotal(33.33);
        order3.setOrderTime(LocalDateTime.now());


        orderService.saveOrder(order3, meals);

        Integer result = orderService.calculateDeliveryTime(5500, 999);

        assertThat(result).isEqualTo(40);



    }



    @AfterEach
    public void cleanUpEach(){
        System.out.println("After Each cleanUpEach() method called");
        orderService.deleteOrder(order);
        orderService.deleteOrder(order1);
        orderService.deleteOrder(order2);
        orderService.deleteOrder(order3);

    }


}
