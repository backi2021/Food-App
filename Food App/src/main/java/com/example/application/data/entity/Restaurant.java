package com.example.application.data.entity;


import com.example.application.data.AbstractEntity;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Restaurant extends AbstractEntity {


    private Integer restaurantOwners_Id;

    private Integer restaurantId;

    private String restaurant_name = "";

    private String restaurant_category = "";

    private Double restaurant_minimum_price = 0d;

    private Double restaurant_delivery_price = 0d;

    private Integer restaurant_zip = 0;

    private String restaurant_city = "";

    private String restaurant_street = "";

    private Double restaurant_radius = 0d;

    public String weeklyDiscount = "";

    private LocalDate startDate;

    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getWeeklyDiscount() {
        return weeklyDiscount;
    }

    public void setWeeklyDiscount(String weeklyDiscount) {
        this.weeklyDiscount = weeklyDiscount;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String mealName) {
        this.restaurant_name = mealName;
    }

    public String getRestaurant_category() {
        return restaurant_category;
    }

    public void setRestaurant_category(String restaurant_category) {
        this.restaurant_category = restaurant_category;
    }

    public Double getRestaurant_minimum_price() {
        return restaurant_minimum_price;
    }

    public void setRestaurant_minimum_price(Double restaurant_minimum_price) {
        this.restaurant_minimum_price = restaurant_minimum_price;
    }

    public Double getRestaurant_delivery_price() {
        return restaurant_delivery_price;
    }

    public void setRestaurant_delivery_price(Double restaurant_delivery_price) {
        this.restaurant_delivery_price = restaurant_delivery_price;
    }

    public Integer getRestaurant_zip() {
        return restaurant_zip;
    }

    public void setRestaurant_zip(Integer restaurant_zip) {
        this.restaurant_zip = restaurant_zip;
    }

    public String getRestaurant_city() {
        return restaurant_city;
    }

    public void setRestaurant_city(String restaurant_city) {
        this.restaurant_city = restaurant_city;
    }

    public String getRestaurant_street() {
        return restaurant_street;
    }

    public void setRestaurant_street(String restaurant_street) {
        this.restaurant_street = restaurant_street;
    }

    public Double getRestaurant_radius() {
        return restaurant_radius;
    }

    public void setRestaurant_radius(Double restaurant_radius) {
        this.restaurant_radius = restaurant_radius;
    }

    public Integer getRestaurantOwners_Id() {
        return restaurantOwners_Id;
    }

    public void setRestaurantOwners_Id(Integer user) {
        this.restaurantOwners_Id = user;
    }
}
