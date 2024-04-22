package com.example.application.views.login;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;


@Route("register")

public class ChooseRegister extends VerticalLayout {


    public ChooseRegister() {

        add(
                new H1("Sind Sie Kunde oder Restaurantbesitzer?"),

                new Button("Als Kunde registrieren", event -> {
                    UI.getCurrent().navigate("customerregister");
                }),
                new Button("Als Restaurantbesitzer registrieren", event -> {
                    UI.getCurrent().navigate("restaurantownerregister");
                })
        );
    }
}



