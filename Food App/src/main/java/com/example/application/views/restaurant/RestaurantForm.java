package com.example.application.views.restaurant;


import com.example.application.data.entity.Favourite;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.FavouriteRepository;
import com.example.application.data.repository.customerRepository.CustomerRepository;
import com.example.application.service.CustomerService;
import com.example.application.service.RestaurantService;
import com.example.application.service.email.Email;
import com.jayway.jsonpath.JsonPath;
import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.layer.map.DefaultMapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.map.MapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.vectors.Circle;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

/* Quelle:  https://vaadin.com/components/
            https://leaflet4vaadin.herokuapp.com/
            https://vaadin.com/directory/component/leaflet4vaadin
            https://docs.microsoft.com/en-us/bingmaps/rest-services/locations/find-a-location-by-address
            https://github.com/json-path/JsonPath
 */

public class RestaurantForm extends FormLayout {

    private Restaurant restaurant;

    private RestaurantService restaurantService;

    // INPUTS
    TextField restaurant_name = new TextField("Name");
    TextField restaurant_category = new TextField("Kategorie");
    TextField restaurant_minimum_price = new TextField("Mindestbestellwert");
    TextField restaurant_delivery_price = new TextField("Lieferkosten");
    TextField restaurant_zip = new TextField("PLZ");
    TextField restaurant_city = new TextField("Stadt");
    TextField restaurant_street = new TextField("Straße");
    TextField restaurant_radius = new TextField("Radius (in km)");

    Customer customer;
    CustomerService customerService;
    CustomerRepository customerRepository;
    FavouriteRepository favouriteRepository;

    Binder<Restaurant> binder = new BeanValidationBinder<>(Restaurant.class);
    LeafletMap map;


    private boolean checkIfFavorite(Integer currentRestaurantId) {
        System.out.println("Methode: checkIfFavorite");
        var favList = favouriteRepository.findAllByCustomerId(customer.getId());

        for (Favourite f : favList) {
            if (f.getRestaurantId() == currentRestaurantId) {
                System.out.println("Ist wahr");
                return true;
            }
        }
        return false;
    }


    // Geburtstag checker
    /*
    public Boolean birthDayChecker(LocalDate ){

    }

     */


    private HorizontalLayout ButtonLayout() {
        Button save = new Button("Speichern");
        Button edit = new Button("Bearbeiten");
        Button delete = new Button("Löschen");

        save.addClickListener(event -> validateAndSave());
        edit.addClickListener(event -> fireEvent(new RestaurantForm.EditEvent(this, restaurant)));
        delete.addClickListener(event -> fireEvent(new RestaurantForm.DeleteEvent(this, restaurant)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        buttonLayout.setFlexGrow(1, save);
        buttonLayout.setFlexGrow(1, edit);
        buttonLayout.setFlexGrow(1, delete);

        buttonLayout.add(save);
        buttonLayout.add(edit);
        buttonLayout.add(delete);

        save.addClickListener(e -> {
            Notification.show("Restaurant erfolgreich gespeichert!");
            setTextFieldEnabled(false);

            RestaurantOwner currentRestaurantOwner = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class);
            //restaurant.setRestaurants_owners_name(currentRestaurantOwner.getFirstName() + " "
                   // + currentRestaurantOwner.getLastName());

            restaurant.setRestaurantOwners_Id(currentRestaurantOwner.getId());

            restaurantService.saveRestaurant(restaurant);

        });
        edit.addClickListener(e -> {
            setTextFieldEnabled(true);
        });
        delete.addClickListener(e -> {
            Notification.show("Restaurant erfolgreich gelöscht!");
            edit.click();
            setTextFieldClear();
        });

        return buttonLayout;
    }

    public String getDay() {
        // Parses the date
        LocalDate dt = LocalDate.parse(LocalDate.now().toString());

        // Prints the day
        System.out.println(dt.getDayOfWeek());

        return dt.toString();
    }

    public LeafletMap leafletMap() {
        LatLng location = new LatLng(getLatitude(), getLongitude());

        MapOptions options = new DefaultMapOptions();
        options.setCenter(location);

        Double radius;

        try {
            radius = restaurant.getRestaurant_radius();
        } catch (Exception e) {
            radius = 3.5;
        }

        if (radius <= 3) {
            options.setZoom(12);
        } else {
            options.setZoom(10);
        }

        LeafletMap leafletMap = new LeafletMap(options);

        leafletMap.setBaseUrl("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

        leafletMap.setWidth("250px");
        leafletMap.setHeight("250px");

        double kilometer = radius * 1000;
        int int_radius = (int) kilometer;

        Circle circle = new Circle(location, int_radius);
        circle.bindPopup("Dein Standort");

        circle.addTo(leafletMap);

        return leafletMap;
    }

    public String getURL() {
        URL location_url;
        String response = "";

        try {


            try {
                location_url = new URL("http://dev.virtualearth.net/REST/v1/Locations/DE/" + restaurant.getRestaurant_zip() + "/" + restaurant.getRestaurant_city().replace(" ", "_") + "/" + restaurant.getRestaurant_street().replace(" ", "_") + "?includeNeighborhood=0&include=ciso2&maxResults=5&key=AvzxCigqjQWsyZMOL8XaV7c_IsFR-hF-DTT4OIlNw8SgcxpaT1tjudHm4Q1-Vukf");

            } catch (Exception e) {
                location_url = new URL("http://dev.virtualearth.net/REST/v1/Locations/DE/45141/Essen/Universitätsstraße9?includeNeighborhood=0&include=ciso2&maxResults=5&key=AvzxCigqjQWsyZMOL8XaV7c_IsFR-hF-DTT4OIlNw8SgcxpaT1tjudHm4Q1-Vukf");


            }


            HttpURLConnection connection = (HttpURLConnection) location_url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String check_line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((check_line = reader.readLine()) != null) {
                response += check_line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Double getLatitude() {
        return JsonPath.read(getURL(), "$.resourceSets[0].resources[0].point.coordinates[0]");
    }

    public Double getLongitude() {
        return JsonPath.read(getURL(), "$.resourceSets[0].resources[0].point.coordinates[1]");
    }

    public RestaurantForm() {
        addClassName("restaurant_form");

        setTextFieldEnabled(false);

        binder.forField(restaurant_zip).withConverter(new StringToIntegerConverter("Hier muss eine Zahl eingetragen werden")).bind(Restaurant::getRestaurant_zip, Restaurant::setRestaurant_zip);

        binder.forField(restaurant_minimum_price).withConverter(new StringToDoubleConverter("Hier muss eine Zahl eingetragen werden")).bind(Restaurant::getRestaurant_minimum_price, Restaurant::setRestaurant_minimum_price);

        binder.forField(restaurant_delivery_price).withConverter(new StringToDoubleConverter("Hier muss eine Zahl eingetragen werden")).bind(Restaurant::getRestaurant_delivery_price, Restaurant::setRestaurant_delivery_price);

        binder.forField(restaurant_radius).withConverter(new StringToDoubleConverter("Hier muss eine Zahl eingetragen werden")).bind(Restaurant::getRestaurant_radius, Restaurant::setRestaurant_radius);

        binder.bindInstanceFields(this);

        map = leafletMap();

        setWidth("500px");
        // einzelne Attribute werden hinzugefügt
        add(restaurant_name, restaurant_category, restaurant_minimum_price, restaurant_delivery_price,
                restaurant_delivery_price, restaurant_zip, restaurant_city, restaurant_street, restaurant_radius,
                ButtonLayout(), map);
    }

    private void setTextFieldEnabled(boolean enabled) {
        restaurant_name.setEnabled(enabled);
        restaurant_category.setEnabled(enabled);
        restaurant_minimum_price.setEnabled(enabled);
        restaurant_delivery_price.setEnabled(enabled);
        restaurant_zip.setEnabled(enabled);
        restaurant_city.setEnabled(enabled);
        restaurant_street.setEnabled(enabled);
        restaurant_radius.setEnabled(enabled);
    }

    private void setTextFieldClear() {
        restaurant_name.clear();
        restaurant_category.clear();
        restaurant_minimum_price.clear();
        restaurant_delivery_price.clear();
        restaurant_zip.clear();
        restaurant_city.clear();
        restaurant_street.clear();
        restaurant_radius.clear();
    }

    private void validateAndSave() {
        try {
            binder.writeBean(restaurant);
            fireEvent(new RestaurantForm.SaveEvent(this, restaurant));
            remove(map);
            map = leafletMap();
            add(map);

        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        binder.readBean(restaurant);
        remove(map);
        map = leafletMap();
        add(map);
    }

    public static abstract class RestaurantFormEvent extends ComponentEvent<RestaurantForm> {
        private Restaurant restaurant;

        protected RestaurantFormEvent(RestaurantForm source, Restaurant restaurant) {
            super(source, false);
            this.restaurant = restaurant;
        }

        public Restaurant getRestaurant() {
            return restaurant;
        }
    }

    public static class SaveEvent extends RestaurantForm.RestaurantFormEvent {
        SaveEvent(RestaurantForm source, Restaurant restaurant) {
            super(source, restaurant);
        }
    }

    public static class EditEvent extends RestaurantForm.RestaurantFormEvent {
        EditEvent(RestaurantForm source, Restaurant restaurant) {
            super(source, restaurant);
        }
    }

    public static class DeleteEvent extends RestaurantForm.RestaurantFormEvent {
        DeleteEvent(RestaurantForm source, Restaurant restaurant) {
            super(source, restaurant);
        }

    }

    public static class CloseEvent extends RestaurantForm.RestaurantFormEvent {
        CloseEvent(RestaurantForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public <T extends ComponentEvent<?>> Registration addChangeValueListener(Class<T> eventType,
                                                                             ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
