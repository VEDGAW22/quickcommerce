package com.example.quickcommerce.models;

import java.util.List;

public class MyOrderDisplay {
    private String orderId;
    private List<String> imageUrls;  // List of image URLs
    private String orderStatus;      // Status of the order (Processing, Confirmed, etc.)
    private String orderDate;        // Date of the order
    private String totalAmount;      // Total amount for the order
    private float rating;            // Optional rating (default value set to 0 if not rated)
    private List<CartOrder.CartItem> cartItems;  // List of cart items in the order (updated to CartItem)

    // Constructor with mandatory fields
    public MyOrderDisplay(String orderId, List<String> imageUrls, String orderStatus,
                          String orderDate, String totalAmount) {
        this.orderId = orderId;
        this.imageUrls = imageUrls;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.rating = 0f; // Default rating if not rated
    }

    // Constructor with additional optional fields
    public MyOrderDisplay(String orderId, List<String> imageUrls, String orderStatus,
                          String orderDate, String totalAmount, float rating,
                          List<CartOrder.CartItem> cartItems) {
        this.orderId = orderId;
        this.imageUrls = imageUrls;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.rating = rating;
        this.cartItems = cartItems;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public float getRating() {
        return rating;
    }

    public List<CartOrder.CartItem> getCartItems() {
        return cartItems;
    }

    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setCartItems(List<CartOrder.CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}

