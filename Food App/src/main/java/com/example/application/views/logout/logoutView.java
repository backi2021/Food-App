package com.example.application.views.logout;

import com.example.application.data.entity.user.Customer;
import com.example.application.service.CustomerService;
import com.example.application.service.DateTimeService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

//Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&t=1609s&ab_channel=vaadinofficial

@Route("logout")
@PageTitle("Logout")
public class logoutView extends Composite<VerticalLayout> {

    DateTimeService dateTimeService;

    public logoutView(CustomerService customerService, DateTimeService dateTimeService){
        this.dateTimeService = dateTimeService;
        dateTimeService.resetToDefaultTime();
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        UI.getCurrent().getPage().setLocation("login");

    }

}
