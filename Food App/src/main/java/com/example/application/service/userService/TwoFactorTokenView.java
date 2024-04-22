package com.example.application.service.userService;

import com.example.application.data.entity.Role;
import com.example.application.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


@Route(value = "TwoFactorTokenView")

public class TwoFactorTokenView extends VerticalLayout {

    public TwoFactorTokenView (AuthService authService){
        setId("Login");
        var twoFactor = new TextField("Two-factor authentication");

        Icon loginIcon = new Icon(VaadinIcon.SIGN_IN);
        Button button = new Button("login  ");
        button.getElement().appendChild(loginIcon.getElement());
        button.setWidth(51, Unit.MM);


        add(
                new H3("2FA"),
                twoFactor,
                button
        );

        button.addClickListener(buttonClickEvent -> {
                    try {
                        Role userRole = authService.twoFactorAuthenticationAndGetUserRole(twoFactor.getValue());
                        if (userRole == Role.KUNDE)
                        {
                            UI.getCurrent().navigate("CustomerView");
                        }
                        else {
                            UI.getCurrent().navigate("RestaurantView");
                        }
                    } catch (AuthService.AuthException e) {
                        Notification.show("Falsche Eingabedaten");
                    }
                }
                );

    }
}
