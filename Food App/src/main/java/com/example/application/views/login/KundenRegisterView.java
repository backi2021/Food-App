package com.example.application.views.login;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.repository.customerRepository.CustomerRepository;
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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

//Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&t=1609s&ab_channel=vaadinofficial
//        https://www.youtube.com/watch?v=ecK9-L9LzxQ&t=376s&ab_channel=vaadinofficial

@Route("customerregister")
public class KundenRegisterView extends Composite{


    private final AuthService authService;
    private final CustomerRepository customerRepository;


    public KundenRegisterView(AuthService authService, CustomerRepository customerRepository){
        this.authService = authService;
        this.customerRepository = customerRepository;
    }

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    TextField email = new TextField("E-Mail");
    DatePicker geburtsdatum = new DatePicker("Geburtsdatum");
    TextField straße = new TextField("Straße");
    TextField postleitzahl = new TextField("Postleitzahl");
    TextField stadt = new TextField("Stadt");
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
                geburtsdatum,
                straße,
                postleitzahl,
                stadt,
                password1,
                password2,



                new Button("Senden", event -> {

                    registerErrorHandling(
                            firstName.getValue(),
                            lastName.getValue(),
                            email.getValue(),
                            geburtsdatum.getValue(),
                            straße.getValue(),
                            postleitzahl.getValue(),
                            stadt.getValue(),
                            password1.getValue(),
                            password2.getValue()

                    );
                })
        );

    }
    private void registerErrorHandling (String firstName,
                                        String lastName,
                                        String email,
                                        LocalDate geburtsdatum,
                                        String straße,
                                        String postleitzahl,
                                        String stadt,
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
        else if (!email.contains(".")) {
            Notification.show("E-Mail eingeben");
        }
        else if (geburtsdatum == null){
            Notification.show("Geburtstag eingeben");
            }
        else if(straße.isEmpty()) {
            Notification.show("Straße eingeben");
        }
        else if(postleitzahl.isEmpty()){
            Notification.show("Postleitzahl eingeben");
        }
        else if(stadt.isEmpty()){
            Notification.show("Stadt eingeben");
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
            Customer customer = new Customer(firstName, lastName, email, password1, Role.KUNDE, "TwoFaktor",geburtsdatum, straße, postleitzahl, stadt, 0,0);
            customerRepository.save(customer);
            Notification.show("Registrierung erfolgreich");
            UI.getCurrent().navigate("login");
        }


    }



}

