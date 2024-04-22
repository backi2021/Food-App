package com.example.application.views.restaurant;

import com.example.application.data.entity.Meal;
import com.example.application.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class RestaurantOrderMeals extends VerticalLayout {

    private OrderService orderService;
    private Grid<Meal> orderMealsGrid = new Grid<>(Meal.class);


    public RestaurantOrderMeals(OrderService orderService) {
        this.orderService = orderService;

        configureOrderMealsGrid();

        add(orderMealsGrid);
    }



    public void updateOrderMealsGrid(int orderId) {
        orderMealsGrid.setItems(orderService.findMealsByOrderId(orderId));
    }




    private void configureOrderMealsGrid() {

        orderMealsGrid.setColumns("mealName", "mealPrice");
        orderMealsGrid.getColumnByKey("mealName").setSortable(false).setHeader("Gericht");
        orderMealsGrid.getColumnByKey("mealPrice").setSortable(false).setHeader("Preis");
        orderMealsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }



}
