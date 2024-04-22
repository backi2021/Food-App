package com.example.application.views.login;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.userRepository.RestaurantOwnerRepository;
import com.example.application.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

//Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&t=1609s&ab_channel=vaadinofficial
//        https://www.youtube.com/watch?v=ecK9-L9LzxQ&t=376s&ab_channel=vaadinofficial

@Route("restaurantownerregister")

public class RestaurantRegisterView extends Composite {


    private final AuthService authService;
    private final RestaurantOwnerRepository restaurantOwnerRepository;


    public RestaurantRegisterView(AuthService authService, RestaurantOwnerRepository restaurantOwnerRepository){
        this.authService = authService;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    TextField email = new TextField("E-Mail");
    PasswordField password1 = new PasswordField("Passwort");
    PasswordField password2 = new PasswordField("Passwort bestätigen");

//initContent: Called when the content of this composite is requested for the first time.
//
//This method should initialize the component structure for the composite and return the root component.
//
//By default, this method uses reflection to instantiate the component based on the generic type of the sub class.
//
//Returns:
//
//the root component which this composite wraps, never null

//Wird aufgerufen, wenn der Inhalt dieses Verbunds zum ersten Mal angefordert wird.
//
//Diese Methode sollte die Komponentenstruktur für den Verbund initialisieren und die Stammkomponente zurückgeben.
//
//Standardmäßig verwendet diese Methode Reflektion, um die Komponente basierend auf dem generischen Typ der Unterklasse zu instanziieren.
//
//Kehrt zurück:
//
//die Wurzelkomponente, die dieses Komposit umhüllt, niemals null

    @Override
    protected Component initContent() {

        return new VerticalLayout(
                new H2("Register"),
                firstName,
                lastName,
                email,
                password1,
                password2,


                new Button("Senden", event -> {

                        registerErrorHandling(
                                firstName.getValue(),
                                lastName.getValue(),
                                email.getValue(),
                                password1.getValue(),
                                password2.getValue()

                        );
                })
        );

    }
    private void registerErrorHandling (String firstName,
                                        String lastName,
                                        String email,
                                        String password1,
                                        String password2){

        if (firstName.isEmpty()){
            Notification.show("Vorname eingeben");
        }
        else if (lastName.isEmpty()){
            Notification.show("Nachname eingeben");
        }
         else if (!email.contains("@")){
            Notification.show("E-Mail eingeben");
        }
         else if (!email.contains(".")){
            Notification.show("E-Mail eingeben");
        }
        else if(password1.isEmpty()){
            Notification.show("Passwort eingeben");
        }
        else if(!password1.equals(password2)){
            Notification.show("Passwörter stimmen nicht überein");
        }
        else if(authService.checkIfEmailIsUsed(email)){
            Notification.show("Email bereits vohanden");
        }

        else{
            RestaurantOwner restaurantOwner = new RestaurantOwner(firstName, lastName,email, password1, Role.RESTAURANTBESITZER, "TwoFaktor");
            restaurantOwnerRepository.save(restaurantOwner);
            Notification.show("Registrierung Erfolgreich");
            UI.getCurrent().navigate("login");
        }


    }



}

