package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;

import javax.persistence.Entity;

@Entity
public class Favourite extends AbstractEntity {

    private Integer restaurantId;

    private Integer customerId;

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
