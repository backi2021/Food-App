package com.example.application.data.entity.user;

import com.example.application.data.AbstractEntity;
import com.example.application.data.entity.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Entity
public class Customer extends AbstractEntity {
    private String firstName;
    private String lastName;
    private LocalDate geburtsdatum;
    private String straße;
    private String postleitzahl;
    private String stadt;


    // favourites
    private String favouriteRestaurant;

    //als unique?
    private double guthabenkonto;
    
    @NotNull
    private double treuepunkte;

    @Column(unique = true)
    private String email;

    private String passwordSalt;
    private String passwordHash;

    private Role role;

    private String activationCode;
    private boolean active;

    private String coupon1;
    private String coupon2;

    private Boolean birthdayDiscount;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Customer() {
    }



    public String getCoupon1() {
        return coupon1;
    }

    public void setCoupon1(String coupon1) {
        this.coupon1 = coupon1;
    }

    public String getCoupon2() {
        return coupon2;
    }

    public void setCoupon2(String coupon2) {
        this.coupon2 = coupon2;
    }


    // Customer ohne Coupons
    public Customer(String firstName, String lastName, String email, String password, Role role,
                    String activationCode, LocalDate geburtsdatum, String straße, String postleitzahl,
                    String stadt, double guthabenkonto, int treuepunkte) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(5);
        this.geburtsdatum = geburtsdatum;
        this.straße = straße;
        this.postleitzahl = postleitzahl;
        this.stadt = stadt;
        this.guthabenkonto = guthabenkonto; //wird der öfter verwendet und muss eine Abfrage wegen Guthabenkonto + Treuepunkt?
        this.treuepunkte = treuepunkte;
    }



    // Customer mit Coupons
    public Customer(String firstName, String lastName, String email, String password, Role role,
                    String activationCode, LocalDate geburtsdatum, String straße, String postleitzahl,
                    String stadt, double guthabenkonto, int treuepunkte,String coupon1, String coupon2) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(5);
        this.geburtsdatum = geburtsdatum;
        this.straße = straße;
        this.postleitzahl = postleitzahl;
        this.stadt = stadt;
        this.guthabenkonto = guthabenkonto; //wird der öfter verwendet und muss eine Abfrage wegen Guthabenkonto + Treuepunkt?
        this.treuepunkte = treuepunkte;
        this.coupon1 = coupon1;
        this.coupon2 = coupon2;
    }

    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String customer_firstName) {
        this.firstName = customer_firstName;
    }

    public void setLastName(String customer_lastName) {
        this.lastName = customer_lastName;
    }

    public String getCustomer_email() {
        return email;
    }

    public void setCustomer_email(String email) {
        this.email = email;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStraße() {
        return straße;
    }

    public void setStraße(String straße) {
        this.straße = straße;
    }

    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }


    public double getGuthabenkonto() {
        return guthabenkonto;
    }

    public void setGuthabenkonto(double guthabenkonto) {
        this.guthabenkonto = guthabenkonto;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public double getTreuepunkte() {
        return treuepunkte;
    }

    public void setTreuepunkte(double treuepunkte) {
        this.treuepunkte = treuepunkte;
    }

    public String getFavouriteRestaurant() {
        return favouriteRestaurant;
    }

    public void setFavouriteRestaurant(String favoriteRestaurant) {
        this.favouriteRestaurant = favoriteRestaurant;
    }

    public Boolean getBirthdayDiscount() {
        return birthdayDiscount;
    }

    public void setBirthdayDiscount(Boolean birthdayDiscount) {
        this.birthdayDiscount = birthdayDiscount;
    }
}

