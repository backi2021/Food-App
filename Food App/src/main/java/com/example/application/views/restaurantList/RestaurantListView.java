package com.example.application.views.restaurantList;

import com.example.application.data.entity.Favourite;
import com.example.application.data.entity.OrderStatus;
import com.example.application.data.entity.Meal;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.repository.FavouriteRepository;
import com.example.application.service.*;
import com.jayway.jsonpath.JsonPath;
import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.layer.groups.LayerGroup;
import com.vaadin.addon.leaflet4vaadin.layer.map.DefaultMapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.map.MapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.ui.marker.Marker;
import com.vaadin.addon.leaflet4vaadin.types.Icon;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.VaadinSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Quellen:
// https://vaadin.com/directory/component/leaflet4vaadin
// https://vaadin.com/docs/v14/ds/components/dialog

@PageTitle("Restaurantauswahl")
@Route("RestaurantListView")
public class RestaurantListView extends VerticalLayout {

    //KOMPONENTEN WERDEN ERSTELLT
    Grid<Restaurant> restaurantGrid = new Grid<>(Restaurant.class, false);

    NumberField mediumQualityRating = new NumberField("Ø Bewertung Qualität");
    NumberField mediumDeliveryRating = new NumberField("Ø Bewertung Lieferung");
    TextField showFavorite = new TextField("Favorit?");
    TextField filterName = new TextField(); //Vielleicht nur ein Feld das beides Durchsucht? Weil zur zeit rufen beide filter Methoden die gleiche Query auf -> Redundanz
    TextField filterCategory = new TextField();
    TextField filterMeal = new TextField();

    IntegerField distancePicker = new IntegerField("Entfernung (km)");

    Button takeOrder = new Button("Essen bestellen");
    Button saveAsFav = new Button("Als Favorit speichern");
    Button filterFavorite = new Button("Nach Favoriten filtern");
    Button resetFilter = new Button("Filter zurücksetzen");


    //SERVICES UND REPOSITORY WERDEN ERSTELLT
    RestaurantService restaurantService;
    CustomerService customerService;
    OrderService orderService;
    MealService mealService;
    Favourite favourite;
    FavouriteRepository favouriteRepository;
    DateTimeService dateTimeService;

    //CUSTOMER UND RESTAURANT WERDEN ERSTELLT
    Customer customer;

    //Die Adresse an die die Bestellung gehen soll
    String street;
    String zip;
    String city;
    Restaurant restaurant;

    LeafletMap map;
    LayerGroup rGroup = new LayerGroup();

    Image discountImage = new Image("images/discount-gif.gif", "images/discount.png");


    public RestaurantListView(RestaurantService restaurantService, CustomerService customerService
            , OrderService orderService, MealService mealservice
            , FavouriteRepository favouriteRepository, DateTimeService dateTimeService) {

        this.restaurantService = restaurantService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.mealService = mealservice;
        this.favouriteRepository = favouriteRepository;
        this.dateTimeService = dateTimeService;

        customer = VaadinSession.getCurrent().getAttribute(Customer.class);
        restaurant = restaurantService.findRestaurant(1);
        favourite = new Favourite();

        mediumDeliveryRating.setWidth("10em");
        mediumDeliveryRating.setEnabled(false);
        mediumQualityRating.setWidth("10em");
        mediumQualityRating.setEnabled(false);
        showFavorite.setWidth("20em");
        showFavorite.setEnabled(false);
        takeOrder.setWidth("20em");
        saveAsFav.setWidth("20em");


        // SAVE as favourite Button and update
        saveAsFav.addClickListener(e -> {
            saveRestaurantAsFav(
                    restaurantGrid.asSingleSelect().getValue(), customer);
            restaurantGrid.getDataProvider().refreshAll();
        });

        takeOrder.addClickListener(e -> goToMenu());


        setSizeFull();

        configureGrid();

        add(getToolbar(), getContent());

        updateRestaurantsByName();
        updateRestaurantsByCategory();
        updateRestaurantsByMeal();
        //updateRatingsByDelivery();
    }

    private void getDialog() {
        discountImage.setVisible(false);
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Create new employee");
        dialog.setOpened(true);
        VerticalLayout dialogLayout = configureDialog(dialog);
        dialog.add(dialogLayout);

        add(dialog);
    }

    // Pop-up
    private VerticalLayout configureDialog(Dialog dialog) {
        H2 headline = new H2("Wohin soll geliefert werden?");

        TextField alternativeStreet = new TextField("Straße");
        TextField alternativeZip = new TextField("PLZ");
        TextField alternativeCity = new TextField("Stadt");

        alternativeStreet.setEnabled(false);
        alternativeZip.setEnabled(false);
        alternativeCity.setEnabled(false);

        com.vaadin.flow.component.icon.Icon standard_icon = new com.vaadin.flow.component.icon.Icon(VaadinIcon.HOME);
        com.vaadin.flow.component.icon.Icon alternative_icon = new com.vaadin.flow.component.icon.Icon(VaadinIcon.HOME_O);

        Button setCurrent = new Button("Standardadresse");
        Button setAlternative = new Button("Alternative Adresse");
        setCurrent.getElement().appendChild(standard_icon.getElement());
        setAlternative.getElement().appendChild(alternative_icon.getElement());

        Button saveButton = new Button("Speichern", e -> dialog.close());
        saveButton.setEnabled(false);

        setCurrent.addClickListener(event -> {
            dialog.close();
            Notification notification = Notification.show("Standardadresse wurde gesetzt");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);

            street = customer.getStraße();
            zip = customer.getPostleitzahl();
            city = customer.getStadt();

            map = leafletMap();
            add(map);
            updateRestaurantsByCategory();
            updateRestaurantsByName();
            updateRestaurantsByMeal();
        });

        setAlternative.addClickListener(event -> {
            setCurrent.setEnabled(false);
            saveButton.setEnabled(true);
            alternativeStreet.setEnabled(true);
            alternativeZip.setEnabled(true);
            alternativeCity.setEnabled(true);
        });

        saveButton.addClickListener(event -> {
            saveButton.setEnabled(true);

            street = alternativeStreet.getValue();
            zip = alternativeZip.getValue();
            city = alternativeCity.getValue();

            Notification notification = Notification.show("Alternative Adresse wurde gesetzt");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);

            map = leafletMap();
            add(map);
            updateRestaurantsByCategory();
            updateRestaurantsByName();
        });

        HorizontalLayout setCurrentButton = new HorizontalLayout(setCurrent);
        HorizontalLayout setAlternativeButton = new HorizontalLayout(setAlternative);
        VerticalLayout textLayout = new VerticalLayout(alternativeStreet,
                alternativeZip, alternativeCity);
        textLayout.setSpacing(false);
        textLayout.setPadding(false);
        textLayout.setAlignItems(FlexComponent.Alignment.STRETCH);


        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(headline, setCurrentButton, setAlternativeButton, textLayout,
                buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }

    //Methode zum Speichern eines Favoriten --> schließt aus, dass doppelt gespeichert wird.
    private void saveRestaurantAsFav(Restaurant currentRestaurant, Customer currentCustomer) {
        // Vielleicht besser in den Service einbinden damit man in der View keine repos benutzt, funktioniert aber, also erstmal lassen

        try {
            Favourite favouriteRestaurant = new Favourite();
            var fav = favouriteRepository.findAllByCustomerId(currentCustomer.getId());
            int currentRestaurantId = currentRestaurant.getId();
            int currentCustomerId = currentCustomer.getId();

            for (Favourite favourite : fav) {
                if (favourite.getRestaurantId() == currentRestaurantId) {
                    return;
                }
            }

            favouriteRestaurant.setRestaurantId(currentRestaurant.getId());
            favouriteRestaurant.setCustomerId(currentCustomer.getId());
            favouriteRepository.save(favouriteRestaurant);
            Notification.show("Als Favorit gespeichert!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (NullPointerException e) {
            System.out.println("Favourite Tabelle ist noch leer");
        }

    }


    private void goToMenu() {
        Ordering newOrder = new Ordering();
        newOrder.setRestaurantId(restaurant.getId());
        newOrder.setRestaurantName(restaurant.getRestaurant_name());
        newOrder.setCustomerId(customer.getId());
        newOrder.setDestinationStreet(street);
        newOrder.setDestinationZip(Integer.parseInt(zip));
        newOrder.setDestinationCity(city);
        newOrder.setStatus(OrderStatus.OPEN);
        newOrder.setEstimateTime(estimateTime());
        VaadinSession.getCurrent().setAttribute(Ordering.class, newOrder);
        UI.getCurrent().navigate("MealChoiceView");
    }

    private Integer estimateTime() {
        Integer distInMeter = restaurantService.calculateDistance(getLatitudeForCustomer(), getLongitudeForCustomer(), getLatitudeForRestaurant(restaurant), getLongitudeForRestaurant(restaurant)).intValue();

        return orderService.calculateDeliveryTime(distInMeter, restaurant.getId());
    }

    private void showIfFavorite(Restaurant currentRestaurant) {
        showFavorite.setValue("");
        int currentRestaurantId = currentRestaurant.getId();
        boolean isFav = checkIfFavorite(currentRestaurantId);

        if (isFav == true) {
            showFavorite.setValue("Ja");
            //showFavorite.setPrefixComponent(VaadinIcon.HEART.create());
        } else {
            showFavorite.setValue("Nein");
        }
    }

    private boolean checkIfFavorite(Integer currentRestaurantId) {
        var favList = favouriteRepository.findAllByCustomerId(customer.getId());

        for (Favourite f : favList) {
            if (f.getRestaurantId() == currentRestaurantId) {
                System.out.println("Ist wahr");
                return true;
            }
        }
        return false;
    }

    private void configureGrid() {
        restaurantGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        restaurantGrid.setSizeFull();
        restaurantGrid.setColumns("restaurant_name", "restaurant_category");
        restaurantGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        restaurantGrid.getColumnByKey("restaurant_name").setHeader("Name");
        restaurantGrid.getColumnByKey("restaurant_category").setHeader("Kategorie");


        restaurantGrid.asSingleSelect().getElement();
        restaurantGrid.asSingleSelect().addValueChangeListener(e ->
        {
            updateRating(restaurantGrid.asSingleSelect().getValue());
            showIfFavorite(restaurantGrid.asSingleSelect().getValue());
            showIfDiscount(restaurantGrid.asSingleSelect().getValue());
            //Notification.show(" Favorit ").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
    }

    private void showIfDiscount(Restaurant currentRestaurant) {
        int currentRestaurantId = currentRestaurant.getId();
        LocalDate today = LocalDate.from(dateTimeService.getLocalDateTime());
        restaurant = restaurantService.findById(currentRestaurantId);

        try {
            if (today.isAfter(restaurant.getStartDate()) && today.isBefore(restaurant.getEndDate())) {
                discountImage.setVisible(true);
            } else if (today.isEqual(restaurant.getStartDate()) || today.isEqual(restaurant.getEndDate())) {
                discountImage.setVisible(true);
            } else if (today.getDayOfWeek().name().equals(restaurant.getWeeklyDiscount())) {
                discountImage.setVisible(true);
            } else {
                discountImage.setVisible(false);
            }
        } catch (Exception e) {
            discountImage.setVisible(false);
        }
    }

    public void updateRating(Restaurant currentRestaurant) {
        restaurant = currentRestaurant;
        mediumQualityRating.setValue(null);
        mediumDeliveryRating.setValue(null);
        int currentRestaurantId = currentRestaurant.getId();
        var orderings = orderService.findAllByRestaurantId(currentRestaurantId);
        double mediumQualityRatingValue = createMediumQualityRating(orderings);
        double mediumDeliveryRatingValue = createMediumDeliveryRating(orderings);
        mediumQualityRating.setValue(mediumQualityRatingValue);
        mediumDeliveryRating.setValue(mediumDeliveryRatingValue);
    }


    public double createMediumQualityRating(List<Ordering> orderings) {
        int anzahl = 0;
        int ratingValue = 0;

        for (Ordering order : orderings) {
            if (order.getRatingSubmitted() == true) {
                anzahl += 1;
                ratingValue = ratingValue + order.getQualityRating();
            }
        }
        return (double) ratingValue / anzahl;
    }


    public double createMediumDeliveryRating(List<Ordering> orderings) {
        int anzahl = 0;
        int ratingValue = 0;

        for (Ordering order : orderings) {
            if (order.getRatingSubmitted() == true) {
                anzahl += 1;
                ratingValue = ratingValue + order.getDeliveryRating();
            }
        }
        return (double) ratingValue / anzahl;
    }


    private HorizontalLayout getToolbar() {
        filterName.setPlaceholder("Nach Name suchen...");
        filterName.setClearButtonVisible(true);
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.addValueChangeListener(e -> updateRestaurantsByName());

        filterCategory.setPlaceholder("Nach Kategorie suchen...");
        filterCategory.setClearButtonVisible(true);
        filterCategory.setValueChangeMode(ValueChangeMode.LAZY);
        filterCategory.addValueChangeListener(e -> updateRestaurantsByCategory());

        filterMeal.setPlaceholder("Nach Speise suchen...");
        filterMeal.setClearButtonVisible(true);
        filterMeal.setValueChangeMode(ValueChangeMode.LAZY);
        filterMeal.addValueChangeListener(e -> updateRestaurantsByMeal());

        distancePicker.setStep(5);
        distancePicker.setMax(200);
        distancePicker.setMin(5);
        distancePicker.setValue(100);
        distancePicker.setHasControls(true);


        filterFavorite.addClickListener(e -> {
            updateRestaurantsByFavorite();
            Notification notification = Notification.show("Restaurants erfolgreich nach Favoriten gefiltert");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);
        });

        resetFilter.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resetFilter.addClickListener(e -> {
            resetFilter();
            Notification notification = Notification.show("Restaurants erfolgreich zurückgesetzt");
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setPosition(Notification.Position.MIDDLE);
        });

        distancePicker.addValueChangeListener(e -> updateRestaurantsByName());

        HorizontalLayout toolbar = new HorizontalLayout(filterName, filterCategory, distancePicker, filterFavorite, resetFilter, filterMeal);
        toolbar.setAlignItems(Alignment.BASELINE);
        return toolbar;
    }

    private HorizontalLayout getContent() {
        map = leafletMap();

        HorizontalLayout content = new HorizontalLayout(mediumQualityRating, mediumDeliveryRating);
        VerticalLayout content2 = new VerticalLayout(content, showFavorite, takeOrder, saveAsFav);
        VerticalLayout content4 = new VerticalLayout(restaurantGrid);
        VerticalLayout content5 = new VerticalLayout(discountImage);
        HorizontalLayout content3 = new HorizontalLayout(content4, content2, content5);
        //   content.setFlexGrow(2, leafletMap());

        content3.setSizeFull();

        getDialog();
        return content3;
    }


    // TODO: Komplett in eine eigene Klasse auslagern
    public LeafletMap leafletMap() {
        LatLng latLng = new LatLng(getLatitudeForCustomer(), getLongitudeForCustomer());

        MapOptions options = new DefaultMapOptions();
        options.setCenter(latLng);
        options.setZoom(10);

        LeafletMap leafletMap = new LeafletMap(options);

        leafletMap.setBaseUrl("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

        leafletMap.setWidth("500px");
        leafletMap.setHeight("500px");

        // Statische Marker zum Testen
        // Customer marker
        Marker marker = new Marker(options.getCenter());
        marker.setDraggable(false);
        marker.bindPopup("Dein Standort");
        marker.setIcon(new Icon("images/customer.png"));

        marker.addTo(leafletMap);

        return leafletMap;
    }

    public String getUrlForCustomer() { // Hier wird nun die Adresse über street, zip und city aufgerufen
        URL getUrl;
        String response = "";


        try {
            try {
                getUrl = new URL("http://dev.virtualearth.net/REST/v1/Locations/DE/" + zip + "/" + city.replace(" ", "_") + "/" + street.replace(" ", "_") + "?includeNeighborhood=0&include=ciso2&maxResults=5&key=AvzxCigqjQWsyZMOL8XaV7c_IsFR-hF-DTT4OIlNw8SgcxpaT1tjudHm4Q1-Vukf");

            } catch (Exception e) {
                getUrl = new URL("http://dev.virtualearth.net/REST/v1/Locations/DE/45141/Essen/Universitätsstraße9?includeNeighborhood=0&include=ciso2&maxResults=5&key=AvzxCigqjQWsyZMOL8XaV7c_IsFR-hF-DTT4OIlNw8SgcxpaT1tjudHm4Q1-Vukf");
            }

            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
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

    public String getUrlForRestaurant(Restaurant r) {
        URL location_url;
        String response = "";

        try {


            try {
                location_url = new URL("http://dev.virtualearth.net/REST/v1/Locations/DE/" + r.getRestaurant_zip() + "/" + r.getRestaurant_city().replace(" ", "_") + "/" + r.getRestaurant_street().replace(" ", "_") + "?includeNeighborhood=0&include=ciso2&maxResults=5&key=AvzxCigqjQWsyZMOL8XaV7c_IsFR-hF-DTT4OIlNw8SgcxpaT1tjudHm4Q1-Vukf");

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

    public Double getLatitudeForCustomer() {
        return JsonPath.read(getUrlForCustomer(), "$.resourceSets[0].resources[0].point.coordinates[0]");
    }

    public Double getLongitudeForCustomer() {
        return JsonPath.read(getUrlForCustomer(), "$.resourceSets[0].resources[0].point.coordinates[1]");
    }

    public Double getLatitudeForRestaurant(Restaurant r) {
        return JsonPath.read(getUrlForRestaurant(r), "$.resourceSets[0].resources[0].point.coordinates[0]");
    }

    public Double getLongitudeForRestaurant(Restaurant r) {
        return JsonPath.read(getUrlForRestaurant(r), "$.resourceSets[0].resources[0].point.coordinates[1]");
    }

    private void updateRestaurantsByName() {
        List<Restaurant> restaurants;
        restaurants = restaurantService.findAllRestaurants(filterName.getValue());
        restaurants = filterForFavoritesAndDistance(restaurants);
        restaurantGrid.setItems(restaurants);

        createRestaurantMarker(restaurants);


    }

    private void createRestaurantMarker(List<Restaurant> restaurants) {
        map.removeLayer(rGroup);
        rGroup = new LayerGroup();
        for (Restaurant r : restaurants) {
            if (restaurantService.findFavouritesOfUser(customer.getId()).contains(r)) {

                Marker marker = new Marker(new LatLng(getLatitudeForRestaurant(r), getLongitudeForRestaurant(r)));
                marker.setDraggable(false);
                marker.bindPopup("★ " + r.getRestaurant_name());
                marker.setIcon(new Icon("images/favorites.png"));
                marker.addTo(rGroup);

            } else {
                Marker marker = new Marker(new LatLng(getLatitudeForRestaurant(r), getLongitudeForRestaurant(r)));
                marker.setDraggable(false);
                marker.bindPopup(r.getRestaurant_name());
                marker.setIcon(new Icon("images/distances.png"));
                marker.addTo(rGroup);

            }


        }

        rGroup.addTo(map);
    }

    private List<Restaurant> filterForFavoritesAndDistance(List<Restaurant> restaurants) {
        List<Restaurant> listOfFavorites = restaurantService.findFavouritesOfUser(customer.getId());
        List<Restaurant> listWithoutFavorites = new ArrayList<>(restaurants);
        listWithoutFavorites.removeAll(listOfFavorites);
        List<Restaurant> listOfCloseRestaurants = findIfRestaurantIsClose(listWithoutFavorites);

        listOfFavorites.addAll(listOfCloseRestaurants);
        restaurants.retainAll(listOfFavorites);
        return restaurants;
    }

    private List<Restaurant> findIfRestaurantIsClose(List<Restaurant> restaurants) {
        List<Restaurant> closeRestaurants = new ArrayList<>();

        for (Restaurant r : restaurants) {
            Double d = restaurantService.calculateDistance(getLatitudeForCustomer(), getLongitudeForCustomer(), getLatitudeForRestaurant(r), getLongitudeForRestaurant(r));
            if (d <= (r.getRestaurant_radius() * 1000) && d <= distancePicker.getValue() * 1000) {
                closeRestaurants.add(r);
            }
        }

        return closeRestaurants;
    }

    private void updateRestaurantsByCategory() {
        List<Restaurant> restaurants;
        restaurants = restaurantService.findAllRestaurants(filterCategory.getValue());
        restaurants = filterForFavoritesAndDistance(restaurants);
        restaurantGrid.setItems(restaurants);

        createRestaurantMarker(restaurants);
    }

    private void updateRestaurantsByFavorite() {
        List<Restaurant> favoriteRestaurants;
        favoriteRestaurants = restaurantService.findFavouritesOfUser(customer.getId());
        //favoriteRestaurants = filterForFavoritesAndDistance(favoriteRestaurants);
        restaurantGrid.setItems(favoriteRestaurants);

        createRestaurantMarker(favoriteRestaurants);
    }

    public void resetFilter() {
        updateRestaurantsByName();
        filterName.setValue("");
        filterCategory.setValue("");
    }

    private void updateRestaurantsByMeal() {
        List<Restaurant> restaurants;
        restaurants = restaurantService.findAll();
        restaurants = filterForFavoritesAndDistance(restaurants);

        var meals = mealService.findAllMealsForSearch(filterMeal.getValue());

        List<Restaurant> filteredRestaurant = new ArrayList<>();

        if (filterMeal.getValue() == null || filterMeal.getValue().isEmpty()) {
            restaurantGrid.setItems(restaurants);
            createRestaurantMarker(restaurants);
        } else {
            for (Meal m : meals) {
                for (Restaurant r : restaurants) {
                    if (m.getRestaurantId() == r.getId()) {
                        if (!filteredRestaurant.contains(r)) {
                            filteredRestaurant.add(r);
                        }
                    }
                }
            }
            restaurantGrid.setItems(filteredRestaurant);
            createRestaurantMarker(filteredRestaurant);
        }
    }
}



