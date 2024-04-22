package com.example.application.views.meal;


import com.example.application.data.entity.Meal;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.service.MealService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.io.IOUtils;

import java.sql.SQLException;


@PageTitle("Ihre Speisen")
public class MealListView extends VerticalLayout {
    Grid<Meal> mealGrid = new Grid<>(Meal.class);
    TextField filterText = new TextField();
    MealForm form;
    MealService mealService;

    MemoryBuffer buffer = new MemoryBuffer();




    public MealListView(MealService mealService) {
        addClassName("list-view");

        this.mealService = mealService;
        mealService.setCurrentRestaurantIdByOwner(VaadinSession.getCurrent().getAttribute(RestaurantOwner.class));





        setSizeFull();
        configureGrid();
        configureForm();



        add(getToolbar(), getContent());

        updateMealList();
    }



    private void updateMealList() {
        mealGrid.setItems(mealService.findAllMeals(filterText.getValue()));
    }

    private void configureGrid() {


        mealGrid.setSizeFull();
        mealGrid.setColumns("mealCategory", "mealName", "mealDetails", "mealPrice");
        mealGrid.getColumns().forEach(col -> col.setAutoWidth(true));


        mealGrid.asSingleSelect().addValueChangeListener(event -> editMeal(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Nach Speise suchen...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        filterText.addValueChangeListener(e -> updateMealList());

        Button addMealButton = new Button("Speise Hinzufügen");
        Upload xmlUpload = new Upload(buffer);
        Button importXmlButton = new Button("Speiseliste als Xml-Datei hochladen");

        xmlUpload.setUploadButton(importXmlButton);
        xmlUpload.setAcceptedFileTypes(".xml");

        xmlUpload.addFileRejectedListener(fileRejectedEvent -> {
            Notification notification = Notification.show("Bitte XML-Datei auswählen!", 2000,Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        });

        xmlUpload.addSucceededListener(event -> {
            try {

                String readContent = new String(IOUtils.toByteArray(buffer.getInputStream()));

                mealService.saveMealsFromXml(readContent);
                updateMealList();

                Notification notification = Notification.show("Upload erfolgreich!", 2000,Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                xmlUpload.getElement().executeJavaScript("this.files=[]");

            } catch (Exception ex){

                Notification notification = Notification.show("Upload nicht erfolgreich!", 2000,Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            }});




        addMealButton.addClickListener(click -> addMeal());



        HorizontalLayout toolbar = new HorizontalLayout(filterText, addMealButton, xmlUpload);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(mealGrid, form);
        content.setFlexGrow(2, mealGrid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new MealForm();
        form.setWidth("25em");
        form.addListener(MealForm.SaveEvent.class, this::saveMeal);
        form.addListener(MealForm.DeleteEvent.class, this::deleteMeal);

    }

    private void saveMeal(MealForm.SaveEvent event) {
        mealService.saveMeal(event.getMeal());
        updateMealList();

    }

    private void deleteMeal(MealForm.DeleteEvent event) {
        mealService.deleteMeal(event.getMeal());
        updateMealList();
    }

    public void editMeal(Meal meal) {
        if (meal != null){

            try {
                form.setMeal(meal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void addMeal() {
        mealGrid.asSingleSelect().clear();
        editMeal(new Meal());
    }



}
