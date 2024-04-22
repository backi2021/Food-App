package com.example.application.views.ordering;


import com.example.application.data.entity.OrderStatus;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.Restaurant;
import com.example.application.data.entity.user.Customer;
import com.example.application.service.CustomerService;
import com.example.application.service.DateTimeService;
import com.example.application.service.OrderService;
import com.example.application.data.entity.Meal;
import com.example.application.service.email.Email;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BasketLayout extends VerticalLayout {

    private Meal meal;
    private List<Meal> meals = new ArrayList<>();
    private Image mealImage = new Image();
    private OrderService orderService;
    private CustomerService customerService;
    private DateTimeService dateTimeService;

    private String mealImageName;

    private Ordering order;

    private Customer customer;

    private Restaurant restaurant;
    private Button addButton = new Button("Speise hinzufügen");
    private Grid<Meal> mealsBasketGrid = new Grid<>(Meal.class);

    private Button sendButton = new Button("Bestellen");

    private TextArea specialText = new TextArea();

    private H5 deliveryCostInfo = new H5("Hier stehen die Lieferkosten!");
    private H5 basketTotal;
    private H5 discount = new H5();
    private H5 mealsTotal;
    private H5 minBasketInfo = new H5("Hier steht der Mindestbestellwert");

    private String discountCode;

    public BasketLayout(OrderService orderService, CustomerService customerService, Ordering order, Restaurant restaurant, DateTimeService dateTimeService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.order = order;
        this.restaurant = restaurant;
        this.dateTimeService = dateTimeService;

        mealImage.setSizeFull();
        configureItemGrid();


        add(mealImage, configureAddButton(), mealsBasketGrid, configureOrderLayout());
        updateItemGrid();

    }


    private VerticalLayout configureOrderLayout() {
        customer = VaadinSession.getCurrent().getAttribute(Customer.class);

        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendButton.setSizeFull();
        specialText.setMaxLength(140);
        specialText.setLabel("Ihre Sonderwünsche:");
        specialText.setSizeFull();

        deliveryCostInfo.setText(restaurant.getRestaurant_name() + " hat eine Liefergebühr von " + restaurant.getRestaurant_delivery_price() + " Euro!");
        minBasketInfo.setText(restaurant.getRestaurant_name() + " hat eine Mindestbestellwert von " + restaurant.getRestaurant_minimum_price() + " Euro!");

        LocalDate today = LocalDate.from(dateTimeService.getLocalDateTime());

        mealsTotal = new H5("Sie haben Ware im Wert von " + getMealsTotal() + " Euro im Warenkorb.");
        basketTotal = new H5("Diese Bestellung kostet sie " + getTotal() + " Euro");

        try {
            if (customer.getBirthdayDiscount() == true) {
                discount.setText("Sie erhalten 20% Rabatt auf ihre Bestellung");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        // Rabatt-Zeitraum Abfrage
        try {
            if (today.isAfter(restaurant.getStartDate()) && today.isBefore(restaurant.getEndDate())) {
                discount.setText("Sie erhalten 20% Rabatt auf ihre Bestellung");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Rabatt-Zeitraum Abfrage an den Grenztagen
        try {
            if (today.isEqual(restaurant.getStartDate()) || today.isEqual(restaurant.getEndDate())) {
                discount.setText("Sie erhalten 20% Rabatt auf ihre Bestellung");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Rabatt-Tag Abfrage
        try {
            if (today.getDayOfWeek().name().equals(restaurant.getWeeklyDiscount())) {
                discount.setText("Sie erhalten 20% Rabatt auf ihre Bestellung");
            } else {
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



        sendButton.addClickListener(event -> {
            try {
                checkOrder();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        VerticalLayout orderLayout = new VerticalLayout();
        orderLayout.add(specialText, deliveryCostInfo, minBasketInfo, mealsTotal, discount, basketTotal, sendButton);

        return orderLayout;
    }

    private void checkOrder() throws Exception {
        if (getTotal() > VaadinSession.getCurrent().getAttribute(Customer.class).getGuthabenkonto()) {
            Notification notification = Notification.show("Ihr Guthaben reicht nicht aus!", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        } else {
            if (getMealsTotal() < restaurant.getRestaurant_minimum_price()) {
                Notification notification = Notification.show("Sie haben den Mindestbestellwert nicht erreicht!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            } else {


                processOrder();

            }
        }

    }

    private void processOrder() throws Exception {
        if (specialText.isEmpty()) {

            order.setStatus(OrderStatus.ACCEPTED);

            updateBonusPoints();
            payOrder();
            sendOrder();
            Notification notification = Notification.show("Bestellung erfolgreich! Sie erhalten Ihre Bestellung in etwa " + order.getEstimateTime() + " Minuten.", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);




        } else {
            order.setStatus(OrderStatus.PENDING);
            order.setSpecials(specialText.getValue());
            updateBonusPoints();
            sendOrder();
            Notification notification = Notification.show("Sie erhalten eine Nachricht wenn Ihre Bestellung vom Restaurant bestätigt wird! Sie erhalten Ihre Bestellung in etwa " + order.getEstimateTime() + " Minuten.", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);



        }


        meals.removeAll(meals);
        updateItemGrid();
        UI.getCurrent().navigate("OrderHistoryView");
    }

    private void payOrder() {
        VaadinSession.getCurrent().getAttribute(Customer.class).setGuthabenkonto(VaadinSession.getCurrent().getAttribute(Customer.class).getGuthabenkonto() - getTotal());
        customerService.saveCustomer(VaadinSession.getCurrent().getAttribute(Customer.class));
    }


    public void sendOrder() {
        order.setOrderTime(dateTimeService.getLocalDateTime());
        order.setOrderTotal(getTotal());

        order.setCustomerEmail(customer.getEmail());


        // Damit nicht NullpointerException gemacht wird
        order.setQualityRating(0);
        order.setDeliveryRating(0);
        order.setReviewText("");
        order.setRatingSubmitted(false);

        orderService.saveOrder(order, meals);


    }

    // getTotal + discount
    public Double getTotal() {
        Double total;
        try {
            total = restaurant.getRestaurant_delivery_price();
        } catch (Exception e) {
            total = 0.0;
        }
        Double discount = (total + getMealsTotal()) * 0.2;

        LocalDate today;

        try {
            today = LocalDate.from(dateTimeService.getLocalDateTime());

        } catch (Exception e) {
            e.printStackTrace();
            today = LocalDate.from(LocalDate.now());
        }


        /*
        // Rabatte
        try {
            if (customer.getBirthdayDiscount() == true) {
                total = total + getMealsTotal() - discount;
                System.out.println("Hier rein gekommen getBirthdayDiscount() ");
                System.out.println("LOCALDATE : "+ LocalDate.now());
                System.out.println("DateTimeService : "+ dateTimeService.getLocalDateTime());
            }
            else if ((restaurant.getStartDate() != null && restaurant.getEndDate() != null))
            {
                if (restaurant.getStartDate().isEqual(LocalDate.from(dateTimeService.getLocalDateTime()))
                        || restaurant.getEndDate().isEqual(LocalDate.from(dateTimeService.getLocalDateTime())))
                {
                    total = total + getMealsTotal() - discount;
                }
                else if (restaurant.getStartDate().isAfter(LocalDate.from(dateTimeService.getLocalDateTime()))
                        && restaurant.getEndDate().isBefore(LocalDate.from(dateTimeService.getLocalDateTime())))
                {
                    total = total + getMealsTotal() - discount;
                    System.out.println("Hier rein gekommen ZeitRaum");
                }
            }
            else if (restaurant.weeklyDiscount.equals(dateTimeService.getLocalDateTime().getDayOfWeek().name()))
                {
                total = total + getMealsTotal() - discount;
                System.out.println("Hier rein gekommen weeklyDiscount ");
                }
            else {
                total = total + getMealsTotal();
                 }
        }
        catch (Exception e) {
            total = total + getMealsTotal();
            System.out.println("In catch rein gekommen ");
        }

         */


        try {
            //Rabatt-Geburstag
            if (customer.getBirthdayDiscount() == true) {
                total = total + getMealsTotal() - discount;
                System.out.println("Total bei Geburtstag : " + total + " Today :" + today);
            }

            // Rabatt-Zeitraum Abfrage
            else if (today.isAfter(restaurant.getStartDate()) && today.isBefore(restaurant.getEndDate())) {

                total = total + getMealsTotal() - discount;
                System.out.println("Total bei Rabatt-Zeitraum Abfrage: " + total + " Today :" + today);
            }

            // Rabatt-Zeitraum Abfrage an den Grenztagen

            else if (today.isEqual(restaurant.getStartDate()) || today.isEqual(restaurant.getEndDate())) {
                total = total + getMealsTotal() - discount;
                System.out.println("Total bei Grenztagen  : " + total + " Today :" + today);
            }

            // Rabatt-Tag Abfrage
            else if (today.getDayOfWeek().name().equals(restaurant.getWeeklyDiscount())) {
                total = total + getMealsTotal() - discount;
                System.out.println("Total Rabatt-Tag : " + total + " Today :" + today);
            }

            else {
                total = total + getMealsTotal();
                System.out.println("Scheiße gebaut");
            }
        }
        catch (Exception e){
            total = total + getMealsTotal();
            System.out.println(e.getMessage());
        }



        return total;
    }

    public Double getMealsTotal() {
        Double subTotal = 0.0;

        try {
            for (Meal m : meals) {
                subTotal = subTotal + m.getMealPrice();
            }
        } catch (Exception e) {
            subTotal = 0.0;
        }

        return subTotal;
    }

    private void updateItemGrid() {
        try {
            mealsBasketGrid.setItems(meals);
        } catch (Exception e) {

        }

    }

    private void configureItemGrid() {
        mealsBasketGrid.setSizeFull();
        mealsBasketGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        mealsBasketGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        // Hier vielleicht noch die sortierpfeile weg
        mealsBasketGrid.setColumns("mealName", "mealPrice");
        mealsBasketGrid.setSortableColumns();
        mealsBasketGrid.addColumn( //Quelle: Vaadin Documentation
                new ComponentRenderer<>(Button::new, (button, meal) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.removeMeal(meal));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Entfernen");

        mealsBasketGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void removeMeal(Meal meal) {
        meals.remove(meal);
        updateItemGrid();
        // Hier noch preis updaten
    }

    private HorizontalLayout configureAddButton() {
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setSizeFull();
        addButton.setEnabled(false);
        addButton.addClickListener(event -> addToBasket());
        HorizontalLayout buttons = new HorizontalLayout(addButton);
        return buttons;
    }

    private void addToBasket() {
        meals.add(meal);
        mealsTotal.setText("Sie haben Ware im Wert von " + getMealsTotal() + " Euro im Warenkorb.");
        basketTotal.setText("Diese Bestellung kostet sie " + getTotal() + " Euro!");
        updateItemGrid();
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
        addButton.setText(meal.getMealName() + " dem Warenkorb hinzufügen");
        addButton.setEnabled(true);


        mealImageName = meal.getMealName() + " Image";

        mealImage.setVisible(false);

        int blobLength = 0;
        try {
            blobLength = (int) meal.getImageData().length();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int finalBlobLength = blobLength;
        mealImage.setSrc(new StreamResource((mealImageName == null) ? "" : mealImageName, () -> {
            try {
                return new ByteArrayInputStream(meal.getImageData().getBytes(1, finalBlobLength));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }));

        mealImage.setVisible(true);

    }


    // Treupunkte

    private void updateBonusPoints() throws Exception {
        updateTreupunkte();
    }


    public double updateTreupunkte() throws Exception {
        Customer thisCustomer = VaadinSession.getCurrent().getAttribute(Customer.class);
        double orderPrice = getTotal();

        for (int i = 0; i < orderPrice; i++) {
            if (orderPrice >= 5) {
                thisCustomer.setTreuepunkte(thisCustomer.getTreuepunkte() + 1);
                orderPrice = orderPrice - 5;
            }
        }
        sendDiscountCode(thisCustomer.getCustomer_email());
        customerService.saveCustomer(thisCustomer);

        return thisCustomer.getTreuepunkte();
    }


    public void sendDiscountCode(String email) throws Exception {
        Email mail = new Email();
        Customer thisCustomer = VaadinSession.getCurrent().getAttribute(Customer.class);

        // double to string
        StringBuilder s = new StringBuilder();
        s.append(thisCustomer.getTreuepunkte());

        int counter = 0;
        while (thisCustomer.getTreuepunkte() >= 10) {
            counter++;
            discountCode = RandomStringUtils.randomAlphanumeric(12);
            if (counter == 1) thisCustomer.setCoupon1(discountCode);
            if (counter == 2) thisCustomer.setCoupon2(discountCode);

            // 1 Gutschein => 5 € => 10 Treupunkte , 1 Treupunkt = 5 €
            mail.sendEmail(email, "Gutscheincode", "Ihr RabattCode:  ", discountCode);
            mail.sendEmail(email, "Treupunkkonto", "Ihre Treupunkte : ", s.toString());
            thisCustomer.setTreuepunkte(thisCustomer.getTreuepunkte() - 10);
            customerService.saveCustomer(thisCustomer);

        }
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setOrder(Ordering order) {
        this.order = order;
    }


}
