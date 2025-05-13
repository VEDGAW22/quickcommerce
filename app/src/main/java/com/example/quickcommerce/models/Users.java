package com.example.quickcommerce.models;

public class Users {
    private String userId;
    private String phone;
    private String someOtherField; // Optional field
    private String userToken;

    // No-argument constructor (required for Firebase)
    public Users() {
    }

    // Constructor with all fields
    public Users(String userId, String phone, String someOtherField, String userToken) {
        this.userId = userId;
        this.phone = phone;
        this.someOtherField = someOtherField;
        this.userToken = userToken;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSomeOtherField() {
        return someOtherField;
    }

    public void setSomeOtherField(String someOtherField) {
        this.someOtherField = someOtherField;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}


