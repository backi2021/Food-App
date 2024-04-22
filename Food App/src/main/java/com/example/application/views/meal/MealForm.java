package com.example.application.views.meal;

import com.example.application.data.entity.Meal;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.io.IOUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

public class MealForm extends FormLayout {
    private Meal meal = new Meal();

    TextField mealName = new TextField("Bezeichnung des Gerichtes");
    TextField mealCategory = new TextField("Kategorie des Gerichtes");
    TextField mealDetails = new TextField("Beschreibung des Gerichtes");
    TextField mealPriceField = new TextField("Preis des Gerichtes");

    Binder<Meal> binder = new BeanValidationBinder<>(Meal.class);

    Button save = new Button("Speichern");
    Button delete = new Button("Löschen");
    Button savePicture = new Button("Bild Speichern");

    MemoryBuffer buffer = new MemoryBuffer(); // Platzhalter
    Upload pictureUpload = new Upload(buffer);
    Button uploadButton = new Button("Bild hochladen");

    private Image mealImage = new Image();

    private String mealImageName;

    private byte[] imageData;





    public MealForm() { // Quelle: offizielle Vaadin Tutorials

        binder.forField(mealPriceField).withConverter(new StringToDoubleConverter("Must enter a number")).bind(Meal::getMealPrice, Meal::setMealPrice);

        binder.bindInstanceFields(this);




        add(mealName, mealCategory, mealDetails, mealPriceField, createPicUploadLayout(), createButtonsLayout());
    }

    private Component createPicUploadLayout() {
        uploadButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        pictureUpload.setUploadButton(uploadButton);
        savePicture.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        pictureUpload.setAcceptedFileTypes("image/jpeg");

        pictureUpload.addSucceededListener(event -> {
            try {
                imageData = IOUtils.toByteArray(buffer.getInputStream());
                mealImageName = meal.getMealName() + " Image";

                Blob blob = new SerialBlob(imageData);
                meal.setImageData(blob);

                mealImage.setSrc(new StreamResource((mealImageName==null)?"":mealImageName, () -> new ByteArrayInputStream(imageData)));
                mealImage.setVisible(true);


            } catch (IOException ex){
                Notification.show("Upload nicht erfolgreich!");

            } catch (SerialException throwables) {
                throwables.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }



        });



        mealImage.setSizeFull();
        
        VerticalLayout uploadLayout = new VerticalLayout(pictureUpload, mealImage);
        return uploadLayout;

    }




    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);



        save.addClickShortcut(Key.ENTER);


        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, meal)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        HorizontalLayout buttons = new HorizontalLayout(save, delete);

        buttons.setFlexGrow(1, save);

        buttons.setFlexGrow(1, delete);

        return buttons;
    }

    private void validateAndSave() {
        try {
            binder.writeBean(meal);
            fireEvent(new SaveEvent(this, meal));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }


    public void setMeal(Meal meal) throws SQLException {
        this.meal = meal;
        binder.readBean(meal);
        if(meal.getMealPrice() == 0.0) mealPriceField.clear();

        mealImageName = meal.getMealName() + " Image";


        mealImage.setVisible(false);

        int blobLength = (int) meal.getImageData().length();
        mealImage.setSrc(new StreamResource((mealImageName==null)?"":mealImageName, () -> {
            try {
                return new ByteArrayInputStream(meal.getImageData().getBytes(1, blobLength));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }));

        mealImage.setVisible(true);


    }

    // Quelle: offizielle Vaadin Tutorials
    public static abstract class MealFormEvent extends ComponentEvent<MealForm> {
        private Meal meal;

        protected MealFormEvent(MealForm source, Meal meal) {
            super(source, false);
            this.meal = meal;
        }

        public Meal getMeal() {
            return meal;
        }
    }

    public static class SaveEvent extends MealFormEvent {
        SaveEvent(MealForm source, Meal meal) {
            super(source, meal);
        }
    }

    public static class DeleteEvent extends MealFormEvent {
        DeleteEvent(MealForm source, Meal meal) {
            super(source, meal);
        }

    }

    public static class CloseEvent extends MealFormEvent {
        CloseEvent(MealForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
