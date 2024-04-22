package com.example.application.views.restaurant;

import com.example.application.data.entity.Favourite;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.FavouriteRepository;
import com.example.application.data.repository.RestaurantRepository;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import com.example.application.service.CustomerService;
import com.example.application.service.RestaurantService;
import com.example.application.service.email.Email;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;


import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@PageTitle("Ihr Restaurant")
public class RestaurantView extends VerticalLayout {

    //
    RestaurantOwner restaurantOwner;
    Restaurant restaurant;
    RestaurantRepository restaurantRepository;
    CustomerRepository customerRepository;
    FavouriteRepository favouriteRepository;
    RestaurantService restaurantService;
    CustomerService customerService;
    RestaurantForm form;

    Button showRatings = new Button("Bewertungen");

    private int restaurantId;

    public RestaurantView(RestaurantService restaurantService, FavouriteRepository favouriteRepository, CustomerRepository customerRepository, CustomerService customerService) {
        this.favouriteRepository = favouriteRepository;
        this.restaurantService = restaurantService;
        this.customerRepository = customerRepository;
        this.customerService = customerService;

        getDialog();

        setSizeFull();
        configureForm();
        add(form, showRatings);
    }


    private void configureForm() {
        form = new RestaurantForm();
        form.setWidth("25em");
        form.addListener(RestaurantForm.SaveEvent.class, this::saveRestaurant);
        //TEST
        form.addChangeValueListener(RestaurantForm.SaveEvent.class, this::saveRestaurant);
        form.addListener(RestaurantForm.DeleteEvent.class, this::deleteRestaurant);
        Integer userId = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class).getId();
        var restaurant = restaurantService.findRestaurant(userId);
        form.setRestaurant(restaurant);
        restaurantId = restaurant.getId();
        showRatings.addClickListener(e -> navigateToRatings());
    }

    //for Pop-up
    private void getDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Create new employee");
        dialog.setOpened(true);
        VerticalLayout dialogLayout = configureDialog(dialog);
        dialog.add(dialogLayout);

        add(dialog);
    }


    // Pop-up für Rabatte // use binder to save the data from datepicker
    private VerticalLayout configureDialog(Dialog dialog) {
        restaurantOwner = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class);
        restaurant = restaurantService.findRestaurant(restaurantOwner.getId());

        H2 headline = new H2("Rabattaktion einrichten");
        H6 zeitraum = new H6("Zeitraum wählen:");
        H6 tag = new H6("Wochentag wählen:");
        Button saveButton = new Button("Speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPlaceholder("Anfang Datum");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPlaceholder("Ende Datum");

        startDatePicker.addValueChangeListener(e ->
                endDatePicker.setMin(e.getValue()));

        endDatePicker.addValueChangeListener(e ->
                endDatePicker.getValue());

        // TextField für Rabattstag
        ComboBox discountDay = new ComboBox();
        discountDay.setItems("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
        discountDay.setPlaceholder("Rabatt-Tag");

        add(startDatePicker, endDatePicker, discountDay);

        VerticalLayout dialogLayout = new VerticalLayout(headline, zeitraum,
                startDatePicker, endDatePicker, tag, discountDay, saveButton);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("height", "400px").set("max-height", "100%");
        dialogLayout.getStyle().set("width", "400px").set("max-width", "100%");

        saveButton.addClickListener(e -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() == null) {
                Notification notification = Notification.show("Wählen Sie ein Enddatum!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.MIDDLE);
            }
            if (endDatePicker.getValue() != null && startDatePicker.getValue() == null) {
                Notification notification = Notification.show("Wählen Sie ein Startdatum!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.MIDDLE);
            }
            if (endDatePicker.getValue() != null && startDatePicker.getValue() != null) {
                restaurant.setStartDate(startDatePicker.getValue());
                restaurant.setEndDate(endDatePicker.getValue());
                Notification.show("Rabattaktion erfolgreich eingerichtet!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                restaurantService.saveRestaurant(restaurant);
            }

            try {
                if (discountDay.getValue() == null || discountDay.getValue().toString().isEmpty()) {
                    restaurant.setWeeklyDiscount(null);
                } else if (discountDay != null && startDatePicker != null && endDatePicker == null) {
                    Notification notification = Notification.show("Wählen Sie ein Enddatum!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.MIDDLE);
                } else if (discountDay != null && endDatePicker != null && startDatePicker == null) {
                    Notification notification = Notification.show("Wählen Sie ein Startdatum!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.MIDDLE);
                } else {
                    restaurant.setWeeklyDiscount(discountDay.getValue().toString());
                    restaurantService.saveRestaurant(restaurant);
                    Notification.show("Rabattaktion erfolgreich eingerichtet!")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    dialog.close();
                }
            } catch (Exception d) {
                restaurant.setWeeklyDiscount(null);
            }

            // E-Mail versenden an Customer, die Favoriten sind

            var cus = customerService.findAll();
            var favourites = favouriteRepository.findAll();

            for (Favourite f : favourites) {
                for (Customer c : cus) {
                    if (f.getRestaurantId() == restaurant.getId()) {
                        if (c.getId() == f.getCustomerId()) {
                            Email email = new Email();
                            try {
                                email.sendEmail(c.getCustomer_email(),
                                        "20% Rabatt warten auf dich"
                                        , "Das Restaurant " + restaurant.getRestaurant_name() + " hat aktuell eine Rabattaktion!"
                                        , "");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
            restaurantService.saveRestaurant(restaurant);
        });

        return dialogLayout;
    }


    private void saveRestaurant(RestaurantForm.SaveEvent event) {
        Restaurant restaurant = event.getRestaurant();
        RestaurantOwner currentRestaurantOwner = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class);
        restaurant.setRestaurantOwners_Id(currentRestaurantOwner.getId());
        restaurantService.saveRestaurant(restaurant);
    }

    private void deleteRestaurant(RestaurantForm.DeleteEvent event) {
        restaurantService.deleteRestaurant(event.getRestaurant());
    }

    public void editRestaurant(Restaurant restaurant) {
        if (restaurant != null) {
            form.setRestaurant(restaurant);
        }
    }

    private void navigateToRatings() {
        // Use query parameters to pass a variable to the next view via additional info in the url.
        // The queryParameter argument in the navigate() method leads to adding a "restaurantId=1337" at the end of the link.
        Map<String, List<String>> queryParams = new Hashtable<>();
        List<String> restaurantIdList = new ArrayList<String>();
        restaurantIdList.add(Integer.toString(restaurantId));
        queryParams.put("restaurantId", restaurantIdList);
        UI.getCurrent().navigate("RatingView", new QueryParameters(queryParams));
    }
}
