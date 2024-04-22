package com.example.application.views.customer;


import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.user.Customer;
import com.example.application.service.OrderService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Ihre Bestellhistorie")
@Route("OrderHistoryView")

public class OrderHistoryView extends VerticalLayout {
    Grid<Ordering> orderGrid = new Grid<>(Ordering.class);

    OrderHistoryMeals orderHistoryMeals;
    OrderHistoryRating orderHistoryRating;
    OrderService orderService;


    public OrderHistoryView(OrderService orderService) {
        addClassName("list-view");

        this.orderService = orderService;


        setSizeFull();
        configureGrid();
        configureOrderHistoryDetails();


        add(orderGrid, getContent());
        updateGrid();
    }





    private void configureGrid() {

        orderGrid.setColumns("orderTime", "restaurantName", "orderTotal");
        orderGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        orderGrid.asSingleSelect().addValueChangeListener(event -> loadOrderingItems(event.getValue()));

        orderGrid.getColumnByKey("orderTime").setHeader("Bestelldatum");
        orderGrid.getColumnByKey("orderTotal").setHeader("Gesamtpreis");

        orderGrid.setSizeFull();

    }

    private void loadOrderingItems(Ordering ordering)
    {
        if ((ordering == null || ordering.getId() == null))
        {
            // Do nothing is no ordering was selected
            return;
        }
        orderHistoryMeals.updateOrderMealsGrid(ordering.getId());
        orderHistoryRating.setRating(ordering);
    }





    private void configureOrderHistoryDetails() {
        orderHistoryMeals = new OrderHistoryMeals(orderService);
        orderHistoryRating = new OrderHistoryRating(orderService);
        orderHistoryMeals.setWidth("40em");
        orderHistoryRating.setWidth("40em");
    }




    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(orderHistoryMeals, orderHistoryRating);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }


    private void updateGrid(){
        orderGrid.setItems(orderService.findAllByCustomerId(VaadinSession.getCurrent().getAttribute(Customer.class).getId()));
    }

}
