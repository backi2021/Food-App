package com.example.application.data.entity;


import com.example.application.data.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;


@Entity
public class Meal extends AbstractEntity {


    @NotEmpty
    private String mealName ="";

    private Integer restaurantId;

    private Integer zaehler;


    @NotEmpty
    private String mealDetails ="";

    @NotEmpty
    private String mealCategory ="";

    public String getMealCategory() {
        return mealCategory;
    }

    public void setMealCategory(String mealCategory) {
        this.mealCategory = mealCategory;
    }


    @Min(value = 0, message ="Geben sie einen Preis höher als 0 Euro an")
    private Double mealPrice = 0d;

    @Lob
    private Blob imageData;

    public Blob getImageData() {
        return imageData;
    }

    public void setImageData(Blob imageData) {
        this.imageData = imageData;
    }


    public byte[] getImage() {
        int blobLength;
        try {
            blobLength = (int) imageData.length();
            return imageData.getBytes(1,blobLength);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    public Integer getZaehler() {
        return zaehler;
    }

    public void setZaehler(Integer zaehler) {
        this.zaehler = zaehler;
    }

    public Integer zaehler(){ return zaehler;}

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDetails() {
        return mealDetails;
    }

    public void setMealDetails(String mealDetails) {
        this.mealDetails = mealDetails;
    }

    public Double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(Double mealPrice) {
        this.mealPrice = (double)Math.round(mealPrice * 100d) / 100d;
    }
}
