package com.example.application.data.entity.user;

import com.example.application.data.AbstractEntity;
import com.example.application.data.entity.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RestaurantOwner extends AbstractEntity {
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String passwordSalt;
    private String passwordHash;
    private Role role;
    private String activationCode;
    private boolean active;

    public RestaurantOwner() {
    }

    public RestaurantOwner(String firstName, String lastName, String email,
                           String password, Role role, String activationCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(5);
    }

    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setUEmail(String email) {
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
}
