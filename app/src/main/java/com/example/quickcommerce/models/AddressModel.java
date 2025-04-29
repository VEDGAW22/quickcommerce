package com.example.quickcommerce.models;

import java.io.Serializable;
import java.util.UUID;

public class AddressModel implements Serializable {

    private String id;
    private String userId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private boolean isDefault;
    private String country;

    // Empty constructor (required for Firebase)
    public AddressModel() {}

    // Full constructor
    // Constructor with fewer parameters
    public AddressModel(String id, String userId, String fullName, String phone,
                        String addressLine, String city, String state, String pincode, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.isDefault = isDefault;
    }




    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    // 👇 Add this method for full address (to fix getAddress() not found error)
    public String getAddress() {
        return addressLine + ", " + city + ", " + state + " - " + pincode;
    }
}
