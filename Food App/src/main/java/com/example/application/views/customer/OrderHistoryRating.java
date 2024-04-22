package com.example.application.views.customer;

import com.example.application.data.entity.OrderStatus;
import com.example.application.data.entity.Ordering;
import com.example.application.data.entity.user.Customer;
import com.example.application.service.OrderService;
import com.jayway.jsonpath.internal.filter.ValueNodes;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.VaadinSession;



public class OrderHistoryRating extends VerticalLayout {

    private Ordering ordering;

    private OrderService orderService;

    private IntegerField qualityRating = new IntegerField("Bewertung Qualität");

    private IntegerField deliveryRating = new IntegerField("Bewertung Lieferung");

    TextArea reviewText = new TextArea("Ihr Kommentar");



    H2 headline = new H2("Bewertung");

    Button rate1 = new Button("Bearbeiten");
    Button rate2 = new Button("Jetzt bewerten");

    Binder<Ordering> binder = new BeanValidationBinder<>(Ordering.class);



    public OrderHistoryRating(OrderService orderService) {
        this.orderService = orderService;


        binder.readBean(ordering);


        setSizeFull();
        configureIntegerFieldQualityRating();
        configureIntegerFieldDeliveryRating();
        configureTextAreaReviewText();
        buttonLayout();
        add(headline, integerFieldLayout(),reviewText, buttonLayout());

    }

    public void configureIntegerFieldQualityRating(){
        qualityRating.setHelperText("max. 5 Sterne");
        qualityRating.setMin(1);
        qualityRating.setMax(5);
        qualityRating.setHasControls(true);
        qualityRating.setWidth("12em");
        qualityRating.setEnabled(false);
    }

    public void configureIntegerFieldDeliveryRating(){
        deliveryRating.setHelperText("max. 5 Sterne");
        deliveryRating.setMin(1);
        deliveryRating.setMax(5);
        deliveryRating.setHasControls(true);
        deliveryRating.setWidth("12em");
        deliveryRating.setEnabled(false);
    }

    public void configureTextAreaReviewText(){
        reviewText.setWidth("25em");
        reviewText.setEnabled(false);
    }

    public void setRating(Ordering ordering)
    {
        this.ordering = ordering;
        qualityRating.setValue(ordering.getQualityRating());
        deliveryRating.setValue(ordering.getDeliveryRating());
        reviewText.setValue(ordering.getReviewText());
        reviewText.setEnabled(false);
        qualityRating.setEnabled(false);
        deliveryRating.setEnabled(false);
    }


    public HorizontalLayout integerFieldLayout() {
        HorizontalLayout integerFields = new HorizontalLayout(qualityRating, deliveryRating);
        return integerFields;
    }

    public HorizontalLayout buttonLayout() {
        HorizontalLayout buttons = new HorizontalLayout(rate1, rate2);
        rate1.setWidth("8em");
        rate1.addClickListener(e -> checkAndClear());
        rate2.setWidth("8em");
        rate2.addClickListener(e -> saveRating());
        return buttons;
    }

    private void checkAndClear(){


            if (ordering.getStatus().equals(OrderStatus.ACCEPTED)) {
                reviewText.setEnabled(true);
                qualityRating.setEnabled(true);
                deliveryRating.setEnabled(true);
            } else {
                Notification.show("Die Bestellung kann noch nicht bewertet werden!");
                reviewText.setEnabled(false);
                qualityRating.setEnabled(false);
                deliveryRating.setEnabled(false);
            }




        }



    //Funktion zum Speichern
    private void saveRating() {

        if (qualityRating.getValue() > 5 || qualityRating.getValue() < 1) {
            Notification.show("Essensqualität Bewertung muss zwischen 1 - 5 sein");
            return;
        }
        if (deliveryRating.getValue() > 5 || deliveryRating.getValue() < 1) {
            Notification.show("Bewertung der Lieferung muss zwischen 1 - 5 sein");
            return;
        }
        reviewText.setEnabled(false);
        qualityRating.setEnabled(false);
        deliveryRating.setEnabled(false);

        ordering.setRatingSubmitted(true);

        ordering.setReviewText(reviewText.getValue());
        ordering.setQualityRating(qualityRating.getValue());
        ordering.setDeliveryRating(deliveryRating.getValue());

        orderService.saveOrdering(ordering);

    }


}
