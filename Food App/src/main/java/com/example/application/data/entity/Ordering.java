package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Ordering extends AbstractEntity {

    private Integer customerId;

    private Integer restaurantId;

    private Boolean ratingSubmitted;

    private String restaurantName;

    private LocalDateTime orderTime;

    private LocalDateTime deliveryTime;

    private Double orderTotal;

    private Integer qualityRating;

    private Integer deliveryRating;

    private String reviewText;

    private OrderStatus status;

    private String destinationStreet;

    private Integer destinationZip;

    private String destinationCity;

    private String favourites;

    private String specials;

    private Integer estimateTime;

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    private String customerEmail;

    public Integer getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(Integer estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getSpecials() {
        return specials;
    }

    public void setSpecials(String specials) {
        this.specials = specials;
    }



    public String getFavourites() {
        return favourites;
    }

    public void setFavourites(String favourites) {
        this.favourites = favourites;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {this.restaurantId = restaurantId;}

    public Boolean getRatingSubmitted() {return ratingSubmitted;}

    public void setRatingSubmitted(Boolean ratingSubmitted) { this.ratingSubmitted = ratingSubmitted;}

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Integer getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }

    public Integer getDeliveryRating() {
        return deliveryRating;
    }

    public void setDeliveryRating(Integer deliveryRating) {
        this.deliveryRating = deliveryRating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDestinationStreet() {
        return destinationStreet;
    }

    public void setDestinationStreet(String destinationStreet) {
        this.destinationStreet = destinationStreet;
    }

    public Integer getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(Integer destinationZip) {
        this.destinationZip = destinationZip;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }
}
