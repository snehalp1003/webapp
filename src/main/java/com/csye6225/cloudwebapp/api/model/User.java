package com.csye6225.cloudwebapp.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Snehal Patel
 *
 */

@Entity
public class User {
    
    @Id   
    private String userEmailAddress;
    private String userPassword;
    private String userFirstName;
    private String userLastName;
    private String uuid;
    
    /**
     * @param userEmailAddress
     * @param userPassword
     * @param userFirstName
     * @param userLastName
     */

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
}
