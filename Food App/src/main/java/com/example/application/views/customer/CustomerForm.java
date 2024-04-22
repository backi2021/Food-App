package com.example.application.views.customer;


import com.example.application.data.entity.user.Customer;
import com.example.application.service.CustomerService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

public class
CustomerForm extends FormLayout {

    private Customer customer;


    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    DatePicker geburtsdatum = new DatePicker("Geburtsdatum");
    TextField straße = new TextField("Straße");
    TextField postleitzahl = new TextField("Postleitzahl");
    TextField stadt = new TextField("Stadt");
    TextField discountInput = new TextField("Rabattcode","Code");
    NumberField guthabenkonto = new NumberField("Ihr Guthaben beträgt...");


    Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);

    CustomerService customerService;


    private HorizontalLayout ButtonLayout() {
        Button save = new Button("Speichern");


        Icon editIcon = new Icon(VaadinIcon.PENCIL);
        Button edit = new Button("Bearbeiten    ");
        edit.getElement().appendChild(editIcon.getElement());


       // Icon discountIcon = new Icon(VaadinIcon.PENCIL);
        Button discountButton = new Button("Rabattcode eingeben");
        //discountButton.getElement().appendChild(discountIcon.getElement());



        /*/ For alternative address
        Icon icon = new Icon(VaadinIcon.HOME_O);
        Button alternativeAddress = new Button("Alternative Adresse  ");
        alternativeAddress.getElement().appendChild(icon.getElement());*/


        save.addClickListener(event -> validateAndSave());

        edit.addClickListener(event ->
                fireEvent(
                        new EditEvent(this, customer)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.addClassName("button-layout");




        //Colors of the button
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        edit.addThemeVariants(ButtonVariant.LUMO_ERROR);
        discountButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        discountButton.setWidth(Float.parseFloat("12"), Unit.CM);
        //alternativeAddress.addThemeVariants(ButtonVariant.LUMO_CONTRAST);


        buttonLayout.setFlexGrow(1, save);
        buttonLayout.setFlexGrow(1, edit);
        //buttonLayout.setFlexGrow(1,alternativeAddress);

        buttonLayout.add(save);
        buttonLayout.add(edit);
        buttonLayout.add(discountButton);



        //  DISCOUNT

        setTextFieldEnabled2(true);

        discountButton.addClickListener(e -> {
            Customer currentCustomer =  VaadinSession.getCurrent().getAttribute(Customer.class);

            if(discountInput.toString() == currentCustomer.getCoupon1() ||
                    currentCustomer.getCoupon1() != null ){
                currentCustomer.setGuthabenkonto(currentCustomer.getGuthabenkonto() + 5);
                currentCustomer.setCoupon1(null);
                customerService.saveCustomer(currentCustomer);
                Notification.show("Rabbatcode wurde eingelöst",2000,
                        Notification.Position.BOTTOM_STRETCH).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                discountInput.clear();
            }
            else if(discountInput.toString() == currentCustomer.getCoupon2() ||
                    currentCustomer.getCoupon2() != null ){
                currentCustomer.setGuthabenkonto(currentCustomer.getGuthabenkonto() + 5);
                currentCustomer.setCoupon2(null);
                customerService.saveCustomer(currentCustomer);
                Notification.show("Rabbatcode wurde eingelöst",2000,
                        Notification.Position.BOTTOM_STRETCH).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                discountInput.clear();
            }
            else
            {
                Notification.show("Ihre Eingabe ist falsch",2000,
                        Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                discountInput.clear();
            }
        });



        save.addClickListener(e -> {
            Notification.show("Kundendaten erfolgreich gespeichert!");
            setTextFieldEnabled(false);
        });
        edit.addClickListener(e -> {
            setTextFieldEnabled(true);
        });

        return buttonLayout;
    }


    private HorizontalLayout LoyaltyPointsLayout() {
        H3 LPText = new H3("Ihre Treuepunkte: ");
        H3 LPValue = new H3(VaadinSession.getCurrent().getAttribute(Customer.class).getTreuepunkte() + "");

        HorizontalLayout LPLayout = new HorizontalLayout();

        LPLayout.setFlexGrow(1, LPText);
        LPLayout.setFlexGrow(1, LPValue);

        LPLayout.add(LPText, LPValue);

        return LPLayout;
    }


    private void setTextFieldEnabled(boolean enabled) {
        firstName.setEnabled(enabled);
        lastName.setEnabled(enabled);
//        email.setEnabled(enabled);
        //password.setEnabled(enabled);
        guthabenkonto.setEnabled(enabled);
        geburtsdatum.setEnabled(enabled);
        straße.setEnabled(enabled);
        postleitzahl.setEnabled(enabled);
        stadt.setEnabled(enabled);
        discountInput.setEnabled(enabled);
    }


    //coupon check
    private void setTextFieldEnabled2(boolean enabled) {
        discountInput.setEnabled(enabled);
    }
    private void setTextFieldClear2(){
        discountInput.clear();
    }


    private void setTextFieldClear() {
        firstName.clear();
        lastName.clear();
//        email.clear();
        //password.clear();
        guthabenkonto.clear();
        geburtsdatum.clear();
        straße.clear();
        postleitzahl.clear();
        stadt.clear();

    }


    private void validateAndSave() {
        try {
            binder.writeBean(customer);

            customerService.saveCustomer(customer);
            VaadinSession.getCurrent().setAttribute(Customer.class, customer);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public CustomerForm(CustomerService customerService) {
        this.customerService = customerService;
        addClassName("customer_lable");

        setTextFieldEnabled(false);

        binder.bindInstanceFields(this);


        setWidth("500px");
        // einzelne Attribute werden hinzugefügt
        add(firstName, lastName /*email*/, /*password,*/ geburtsdatum,
                straße, postleitzahl, stadt, guthabenkonto,discountInput,
                //formLayout,
                ButtonLayout() /*CreditLayout1(), CreditLayout2()*/, LoyaltyPointsLayout());

        //Aktueller Customer wird gesetzt und dann mit dem Binder ausgelesen
        customer = VaadinSession.getCurrent().getAttribute(Customer.class);
        binder.readBean(customer);

    }


    // Wird nicht benötigt weil ist immer der gleiche Customer
    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.readBean(customer);
    }


    // Die Events werden nicht benötigt
    public static abstract class CustomerFormEvent extends ComponentEvent<CustomerForm> {
        private Customer customer;

        protected CustomerFormEvent(CustomerForm source, Customer customer) {
            super(source, false);
            this.customer = customer;
        }


        public Customer getCustomer() {
            return customer;
        }
    }


    public static class SaveEvent extends CustomerForm.CustomerFormEvent {
        SaveEvent(CustomerForm source, Customer customer) {
            super(source, customer);
        }
    }


    public static class EditEvent extends CustomerForm.CustomerFormEvent {
        EditEvent(CustomerForm source, Customer customer) {
            super(source, customer);
        }
    }


    public static class DeleteEvent extends CustomerForm.CustomerFormEvent {
        DeleteEvent(CustomerForm source, Customer customer) {
            super(source, customer);
        }

    }


    public static class CloseEvent extends CustomerForm.CustomerFormEvent {
        CloseEvent(CustomerForm source) {
            super(source, null);
        }
    }


    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}


