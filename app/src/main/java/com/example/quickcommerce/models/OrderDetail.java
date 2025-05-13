package com.example.quickcommerce.models;

import java.util.List;

public class OrderDetail {
    private String orderId;
    private String orderStatus;
    private List<OrderItem> orderedItems;
    private double totalAmount;

    public OrderDetail() {
        // Required by Firebase
    }

    // ✅ Add this constructor
    public OrderDetail(String orderId, String orderStatus, List<OrderItem> orderedItems, double totalAmount) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderedItems = orderedItems;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<OrderItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
