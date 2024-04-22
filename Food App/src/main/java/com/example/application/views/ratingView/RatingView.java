package com.example.application.views.ratingView;
import com.example.application.data.entity.Meal;
import com.example.application.data.entity.Ordering;
import com.example.application.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.*;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.beans.IntrospectionException;
import java.util.*;


@Route("RatingView")
public class RatingView extends VerticalLayout implements HasUrlParameter<String> {

    Grid<Ordering> ratingGrid = new Grid<>(Ordering.class);

    private int restaurantId;

    OrderService orderService;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        // Read the parameter passed in the url (Look at RestaurantView to see how that parameter gets passed.
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        restaurantId = Integer.parseInt(parametersMap.get("restaurantId").get(0));

        UpdateViewContent(restaurantId);
    }


    public RatingView (OrderService orderService) {
        this.orderService = orderService;


        setSizeFull();
        configureRatingGrid();
        add(ratingGrid);
    }

    private void UpdateViewContent(int restaurantId)
    {
        var orderings = orderService.findAllByRestaurantId(restaurantId);
        List<Ordering> ordersWithSubmittedRatings = new ArrayList<Ordering>();

        for (Ordering order : orderings) {
            if (order.getRatingSubmitted() == true)
            {
                ordersWithSubmittedRatings.add(order);
            }
        }


        ratingGrid.setItems(ordersWithSubmittedRatings);
    }



    private void configureRatingGrid() {
        ratingGrid.setColumns("qualityRating", "deliveryRating", "reviewText");
        ratingGrid.getColumns().forEach(col -> col.setAutoWidth(true));


        ratingGrid.getColumnByKey("qualityRating").setHeader("Bewertung Qualität").setSortable(false);
        ratingGrid.getColumnByKey("deliveryRating").setHeader("Bewertung Lieferung").setSortable(false);
        ratingGrid.getColumnByKey("reviewText").setHeader("Kommentar").setSortable(false);

        ratingGrid.setSizeFull();

    }
}
