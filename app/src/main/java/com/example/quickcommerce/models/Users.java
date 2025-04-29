package com.example.quickcommerce.models;

public class Users {
    private String userId;
    private String phone;
    private String someOtherField; // Replace or remove as needed

    // No-argument constructor (required for Firebase serialization)
    public Users() {
    }

    // Constructor with fields
    public Users(String userId, String phone, String someOtherField) {
        this.userId = userId;
        this.phone = phone;
        this.someOtherField = someOtherField;
    }

    // Getters and setters for all properties
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
}

