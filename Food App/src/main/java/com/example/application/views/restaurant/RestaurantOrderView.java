package com.example.application.views.restaurant;

import com.example.application.data.entity.OrderStatus;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.service.CustomerService;
import com.example.application.service.OrderService;
import com.example.application.service.RestaurantService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;



@PageTitle("Ihre Bestellungen")
@Route("RestaurantOrderView")
public class RestaurantOrderView extends VerticalLayout {
    Grid<Ordering> orderGrid = new Grid<>(Ordering.class);

    OrderService orderService;
    RestaurantService restaurantService;
    CustomerService customerService;
    RestaurantOrderMeals restaurantOrderMeals;
    RestaurantOrderRating restaurantOrderRating;
    Ordering selectedOrder;
    NumberField acceptNumberField = new NumberField();
    H4 wantedSpecials = new H4();
    Button acceptSpecialsButton = new Button("Sonderwünsche annehmen");
    Button rejectSpecialsButton = new Button("Sonderwünsche ablehnen");
    TextField rejectTextField = new TextField();
    //TextField specials = new TextField("Sonderwünsche");



    public RestaurantOrderView(OrderService orderService, CustomerService customerService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;


        setSizeFull();
        configureGrid();
        configureOrderHistoryDetails();
        clearAndDisableSpecialInteraction();



        add(orderGrid, getContent());
        updateGrid();

    }

    private VerticalLayout configureSpecials() {
        VerticalLayout specialsLayout = new VerticalLayout();





        acceptNumberField.setLabel("Extrakosten");
        acceptNumberField.setHelperText("Max 25% des Bestellwertes");
        acceptNumberField.setMin(0.0);
        acceptNumberField.setStep(0.25);
        Div euroSuffix = new Div();
        euroSuffix.setText("€");
        acceptNumberField.setSuffixComponent(euroSuffix);
        acceptNumberField.setValue(0.0);
        acceptNumberField.setHasControls(true);


        rejectTextField.setHelperText("Helfen Sie dem Kunden bei seiner nächsten Bestellung");
        rejectTextField.setLabel("Ablehnungsgrund");

        acceptNumberField.setSizeFull();
        rejectTextField.setSizeFull();

        acceptSpecialsButton.setSizeFull();
        rejectSpecialsButton.setSizeFull();

        acceptSpecialsButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        rejectSpecialsButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        acceptSpecialsButton.addClickListener(e -> acceptOrder());
        rejectSpecialsButton.addClickListener(e -> rejectOrder());



        VerticalLayout acceptLayout = new VerticalLayout(acceptNumberField, acceptSpecialsButton);



        VerticalLayout rejectLayout = new VerticalLayout(rejectTextField, rejectSpecialsButton);

        acceptLayout.setSizeFull();
        rejectLayout.setSizeFull();

        VerticalLayout interaction = new VerticalLayout(acceptLayout, rejectLayout);

        interaction.setSizeFull();

        specialsLayout.add(new H2("Der Kunde hat folgende Sonderwünsche:"), wantedSpecials, interaction);

        specialsLayout.setSizeFull();

        return specialsLayout;



    }

    private void rejectOrder() {


        orderService.rejectOrder(selectedOrder, rejectTextField.getValue());

        Notification notification = Notification.show("Bestellung abgelehnt!", 2000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        clearAndDisableSpecialInteraction();

        updateGrid();

    }

    private void acceptOrder() {
        if (tryToPay() == true) {

            Notification notification = Notification.show("Bestellung bestätigt und Bezahlung erfolgreich!", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            clearAndDisableSpecialInteraction();

            updateGrid();

        } else {

            Notification notification = Notification.show("Bezahlung nicht möglich!", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            clearAndDisableSpecialInteraction();

            updateGrid();

        }

    }

    private void clearAndDisableSpecialInteraction() {
        acceptSpecialsButton.setEnabled(false);
        rejectSpecialsButton.setEnabled(false);
        acceptNumberField.setEnabled(false);
        rejectTextField.setEnabled(false);

        rejectTextField.clear();
        acceptNumberField.clear();
    }

    private boolean tryToPay() {
        return orderService.payOrder(selectedOrder, acceptNumberField.getValue());
    }

    private void configureGrid() {

        orderGrid.setColumns("status", "orderTime", "customerId", "orderTotal", "specials");
        orderGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        orderGrid.asSingleSelect().addValueChangeListener(event -> loadOrderingItems(event.getValue()));


        orderGrid.getColumnByKey("status").setHeader("Status");
        orderGrid.getColumnByKey("orderTime").setHeader("Bestelldatum");
        orderGrid.getColumnByKey("orderTotal").setHeader("Gesamtpreis");
        orderGrid.getColumnByKey("customerId").setHeader("Kunden ID");

        orderGrid.setSizeFull();
    }


    private void loadOrderingItems(Ordering ordering)
    {
        if ((ordering == null || ordering.getId() == null))
        {
            // Do nothing if no ordering was selected
            return;
        }
        selectedOrder = ordering;
        wantedSpecials.setText(selectedOrder.getSpecials());

        if ( ! selectedOrder.getStatus().equals(OrderStatus.PENDING)) {
            clearAndDisableSpecialInteraction();

        } else
        {
            acceptSpecialsButton.setEnabled(true);
            rejectSpecialsButton.setEnabled(true);
            acceptNumberField.setEnabled(true);
            rejectTextField.setEnabled(true);

        }
        acceptNumberField.setMax((selectedOrder.getOrderTotal()/4));
        restaurantOrderMeals.updateOrderMealsGrid(ordering.getId());
        restaurantOrderRating.setRating(ordering);
       // specials.setValue(configureSpecials());
    }

    private void configureOrderHistoryDetails() {
        restaurantOrderMeals = new RestaurantOrderMeals(orderService);
        restaurantOrderRating = new RestaurantOrderRating(orderService);
        restaurantOrderMeals.setWidth("40em");
        restaurantOrderRating.setWidth("40em");
    }




    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(restaurantOrderMeals, restaurantOrderRating, configureSpecials());
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }


    private void updateGrid(){
        int restaurantOwnerId = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class).getId();
        int restaurantId = restaurantService.findRestaurant(restaurantOwnerId).getId();
        orderGrid.setItems(orderService.findAllByRestaurantId(restaurantId));
    }


}
