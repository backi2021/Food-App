package com.example.application.views.ordering;


import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import com.example.application.service.*;
import com.example.application.data.entity.Meal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@PageTitle("Ihre Auswahl")
@Route("MealChoiceView")
public class OrderListView extends VerticalLayout {
    Grid<Meal> mealGrid = new Grid<>(Meal.class);
    MealService mealService;
    RestaurantService restaurantService;
    CustomerService customerService;
    OrderService orderService;
    Customer customer;
    Restaurant restaurant;
    Ordering order;
    DateTimeService dateTimeService;
    TextField filterText = new TextField();
    BasketLayout basket;
    Button showRatings = new Button("Alle Bewertungen");



    public OrderListView(MealService mealService, RestaurantService restaurantService, OrderService orderService, CustomerService customerService, DateTimeService dateTimeService){
        this.mealService = mealService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.customerService = customerService;
        this.dateTimeService = dateTimeService;

        customer = VaadinSession.getCurrent().getAttribute(Customer.class);

        /*try {
            restaurant = restaurantService.findById(VaadinSession.getCurrent().getAttribute(Ordering.class).getRestaurantId());
        } catch (Exception e) {
            restaurant = restaurantService.findRestaurant(88);
        }*/
        Integer resId = VaadinSession.getCurrent().getAttribute(Ordering.class).getRestaurantId();
        restaurant = restaurantService.findById(resId);
        order = VaadinSession.getCurrent().getAttribute(Ordering.class);

        mealService.setCurrentRestaurantId(restaurant.getId());

        setSizeFull();
        configureGrid();
        configureBasket();
        H2 header = new H2("Ihre Bestellung bei " + restaurant.getRestaurant_name() + ":");
        add(header,getToolbar(), getContent());

        updateMealList();

    }

    private void configureBasket() {
        basket = new BasketLayout(orderService, customerService,order,restaurant,dateTimeService);
        basket.setWidth("53em");
    }

    private void configureGrid() {
        mealGrid.setSizeFull();
        mealGrid.setColumns("mealCategory", "mealName", "mealDetails", "mealPrice");
        mealGrid.getColumns().forEach(col -> col.setAutoWidth(true));


        mealGrid.asSingleSelect().addValueChangeListener(event -> selectMeal(event.getValue()));

    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Nach Speise suchen...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        filterText.addValueChangeListener(e -> updateMealList());

        Map<String, List<String>> queryParams = new Hashtable<>();
        List<String> restaurantIdList = new ArrayList<String>();
        restaurantIdList.add(restaurant.getId().toString());
        queryParams.put("restaurantId", restaurantIdList);

        showRatings.addClickListener(e -> UI.getCurrent().navigate("RatingView", new QueryParameters(queryParams)));
        showRatings.setWidth("25em"); //größerer Button
        showRatings.setAutofocus(true);





        HorizontalLayout toolbar = new HorizontalLayout(filterText, showRatings);
        toolbar.setAlignItems(Alignment.CENTER);
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(mealGrid, basket);
        content.setFlexGrow(2, mealGrid);
        content.setFlexGrow(1, basket);

        content.setSizeFull();
        return content;
    }

    private void updateMealList() {
        mealGrid.setItems(mealService.findAllMeals(filterText.getValue()));
    }

    private void selectMeal(Meal value) {
        basket.setMeal(value);
    }

}
