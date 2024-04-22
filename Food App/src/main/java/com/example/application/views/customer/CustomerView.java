package com.example.application.views.customer;

import com.example.application.data.entity.user.Customer;
/*import com.example.application.service.CustomerService;*/
import com.example.application.service.CustomerService;
import com.example.application.service.DateTimeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@PageTitle("Ihr KundenKonto")
@Route("CustomerView")
public class CustomerView extends VerticalLayout {


    CustomerService customerService;
    DateTimeService dateTimeService;
    CustomerForm form;
    Customer customer;

    public CustomerView(CustomerService customerService, DateTimeService dateTimeService) {
        this.customerService = customerService;
        this.dateTimeService = dateTimeService;
        changeSystemTime();

        setSizeFull();
        configureForm();


        add(form);
    }


    private void configureForm() {
        form = new CustomerForm(customerService);
        form.setWidth("25em");
        form.addListener(CustomerForm.SaveEvent.class, this::saveCustomer);
        form.addListener(CustomerForm.DeleteEvent.class, this::deleteCustomer);
        Integer userId = VaadinSession.getCurrent().getAttribute(Customer.class).getId();
        /*form.setCustomer(customerService.findCustomer(userId));*/
    }

    private void saveCustomer(CustomerForm.SaveEvent event) {
        Customer customer = event.getCustomer();
        Customer currentCustomer = VaadinSession.getCurrent().getAttribute(Customer.class);

        //customer.setUser(currentCustomer.getId());
        customerService.saveCustomer(customer);
    }

    private void deleteCustomer(CustomerForm.DeleteEvent event) {
        customerService.deleteCustomer(event.getCustomer());
    }

    public void editCustomer(Customer customer) {
        if (customer != null) {
            form.setCustomer(customer);
        }
    }

    public void changeSystemTime() {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.getElement().setAttribute("aria-label", "Create new employee");
        dialog.setOpened(true);

        VerticalLayout dialogLayout = configureSystemTime(dialog);
        dialog.add(dialogLayout);

        add(dialog);
    }

    private VerticalLayout configureSystemTime(Dialog dialog) {
        H2 headline = new H2("Systemzeit ändern");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold").set("text-align", "center");

        DatePicker datePicker = new DatePicker();
        datePicker.setEnabled(false);

        com.vaadin.flow.component.icon.Icon defaultIcon = new com.vaadin.flow.component.icon.Icon(VaadinIcon.CLOCK);
        com.vaadin.flow.component.icon.Icon changeIcon = new com.vaadin.flow.component.icon.Icon(VaadinIcon.CALENDAR_CLOCK);

        Button defaultTime = new Button("Aktuelle Systemzeit übernehmen");
        defaultTime.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        defaultTime.getElement().appendChild(defaultIcon.getElement());

        Button changeTime = new Button("Systemzeit ändern");
        changeTime.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        changeTime.getElement().appendChild(changeIcon.getElement());

        Button save = new Button("Speichern");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        save.setEnabled(false);

        var customer = customerService.findAll();


        defaultTime.addClickListener(e -> {
            dialog.close();

            LocalDate date = LocalDate.now();
            dateTimeService.overWriteDefaultTime(LocalDateTime.now());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/uuuu");
            String time = date.format(dateTimeFormatter);

            Notification notification = Notification.show("Systemzeit wurde auf " + time + " gesetzt.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);

            // Birthday Check
            for (Customer c : customer) {
                if (c.getGeburtsdatum().getMonth() == date.getMonth() && c.getGeburtsdatum().getDayOfMonth() == date.getDayOfMonth()) {
                    c.setBirthdayDiscount(true);
                } else {
                    c.setBirthdayDiscount(false);

                }
                customerService.saveCustomer(c);
            }
        });

        changeTime.addClickListener(e -> {
            datePicker.setEnabled(true);
            save.setEnabled(true);
        });

        save.addClickListener(e -> {
            LocalDateTime ldt = LocalDateTime.of(datePicker.getValue().getYear(), datePicker.getValue().getMonth(), datePicker.getValue().getDayOfMonth(), 12, 0, 0);
            dateTimeService.overWriteDefaultTime(ldt);
            dialog.close();

            LocalDate date = LocalDate.of(datePicker.getValue().getYear(), datePicker.getValue().getMonth(), datePicker.getValue().getDayOfMonth());
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
            String time = date.format(formatters);

            //Birthday
            for (Customer c : customer) {
                if (c.getGeburtsdatum().getMonth() == datePicker.getValue().getMonth()
                        && c.getGeburtsdatum().getDayOfMonth() == datePicker.getValue().getDayOfMonth()) {
                    c.setBirthdayDiscount(true);

                } else {
                    c.setBirthdayDiscount(false);
                }
                customerService.saveCustomer(c);
            }


            Notification notification = Notification.show("Systemzeit wurde auf " + time + " gesetzt.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.MIDDLE);
        });

        HorizontalLayout setDate = new HorizontalLayout(datePicker);
        setDate.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout buttonLayout1 = new HorizontalLayout(defaultTime);
        buttonLayout1.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout buttonLayout2 = new HorizontalLayout(changeTime);
        buttonLayout2.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout buttonLayout3 = new HorizontalLayout(save);
        buttonLayout3.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(headline, buttonLayout1, buttonLayout2, setDate, buttonLayout3);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }
}
