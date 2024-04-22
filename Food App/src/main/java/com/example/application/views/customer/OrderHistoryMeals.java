package com.example.application.views.customer;

import com.example.application.data.entity.Meal;
import com.example.application.data.entity.OrderItem;
import com.example.application.data.entity.Ordering;
import com.example.application.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryMeals extends VerticalLayout{

    private OrderService orderService;

    private Grid<Meal> orderMealsGrid = new Grid<>(Meal.class);






    public OrderHistoryMeals(OrderService orderService){
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

