package com.example.application.views.login;

import com.example.application.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.themes.LumoDarkTheme;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import java.util.*;
//Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&t=1609s&ab_channel=vaadinofficial
//        https://www.youtube.com/watch?v=ecK9-L9LzxQ&t=376s&ab_channel=vaadinofficial
//        https://vaadin.com/docs/latest/flow/tutorials/in-depth-course/login-and-authentication

@Route("login")
@RouteAlias("")
@PageTitle("Login")
@CssImport("./views/login/login-view.css")

public class LoginView extends Div {



    public LoginView(AuthService authService){


        Button loginButton = new Button("Login     ");
        Icon icon = new Icon(VaadinIcon.SIGN_IN);
        loginButton.getElement().appendChild(icon.getElement());




        setId("login-view");

        var email = new TextField("E-mail" );
        var password = new PasswordField("Passwort");



        add(
            new H1("Supreme Eating Program"),
            email,
            password,
            loginButton,
                new RouterLink("register", ChooseRegister.class)
                );



        loginButton.addClickListener(buttonClickEvent -> {
            try {
                authService.authenticate(email.getValue(), password.getValue());

                Map<String, List<String>> queryParams = new Hashtable<>();
                List<String> emailStringList = new ArrayList<String>();
                emailStringList.add(email.getValue());
                queryParams.put("email", emailStringList);

                UI.getCurrent().navigate("TwoFactorTokenView", new QueryParameters(queryParams));
            } catch (AuthService.AuthException e) {
                Notification.show("Falsche Eingabedaten");
            }
        });



        }
    }

