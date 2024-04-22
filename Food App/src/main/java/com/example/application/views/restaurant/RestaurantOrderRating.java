package com.example.application.views.restaurant;

import com.example.application.data.entity.Ordering;
import com.example.application.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class RestaurantOrderRating extends VerticalLayout {

    private Ordering ordering;

    private OrderService orderService;

    private IntegerField qualityRating = new IntegerField("Bewertung Qualität");

    private IntegerField deliveryRating = new IntegerField("Bewertung Lieferung");

    TextArea reviewText = new TextArea("Ihr Kommentar");



    H2 headline = new H2("Bewertung");

    Button akzeptieren = new Button("Akzeptieren");
    Button ablehnen = new Button("Ablehnen");

    Binder<Ordering> binder = new BeanValidationBinder<>(Ordering.class);



    public RestaurantOrderRating(OrderService orderService) {
        this.orderService = orderService;


        binder.readBean(ordering);


        setSizeFull();
        configureIntegerFieldQualityRating();
        configureIntegerFieldDeliveryRating();
        configureTextAreaReviewText();
        buttonLayout();
        add(headline, integerFieldLayout(),reviewText);

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
    }


    public HorizontalLayout integerFieldLayout() {
        HorizontalLayout integerFields = new HorizontalLayout(qualityRating, deliveryRating);
        return integerFields;
    }

    public HorizontalLayout buttonLayout() {
        HorizontalLayout buttons = new HorizontalLayout(akzeptieren, ablehnen);
        akzeptieren.setWidth("8em");
        akzeptieren.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
       // akzeptieren.addClickListener(e -> checkAndClear());
        ablehnen.setWidth("8em");
        ablehnen.addThemeVariants(ButtonVariant.LUMO_ERROR);
       // ablehnen.addClickListener(e -> saveRating());
        return buttons;
    }

    //
   /* private void checkAndClear(){



        reviewText.setEnabled(true);
        qualityRating.setEnabled(true);
        deliveryRating.setEnabled(true);


    }*/


    //Funktion zum Speichern
//    private void saveRating() {
//
//        if (qualityRating.getValue() > 5 || qualityRating.getValue() < 1) {
//            Notification.show("Essensqualität Bewertung muss zwischen 1 - 5 sein");
//            return;
//        }
//        if (deliveryRating.getValue() > 5 || deliveryRating.getValue() < 1) {
//            Notification.show("Bewertung der Lieferung muss zwischen 1 - 5 sein");
//            return;
//        }
//        reviewText.setEnabled(false);
//        qualityRating.setEnabled(false);
//        deliveryRating.setEnabled(false);
//
//        ordering.setRatingSubmitted(true);
//
//        ordering.setReviewText(reviewText.getValue());
//        ordering.setQualityRating(qualityRating.getValue());
//        ordering.setDeliveryRating(deliveryRating.getValue());
//
//        orderService.saveOrdering(ordering);
//
//    }


}
