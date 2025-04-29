package com.example.quickcommerce.models;

import java.util.List;

public class CartOrder {

    // Static inner class for CartItem
    public static class CartItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;

        // Constructor
        public CartItem(String productId, String productName, int quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    // Static inner class for Order
    public static class Order {
        private String orderId;
        private String userId;
        private List<CartItem> cartItems;
        private double totalAmount;

        // Constructor
        public Order(String orderId, String userId, List<CartItem> cartItems, double totalAmount) {
            this.orderId = orderId;
            this.userId = userId;
            this.cartItems = cartItems;
            this.totalAmount = totalAmount;
        }

        // Getters and Setters
        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<CartItem> getCartItems() {
            return cartItems;
        }

        public void setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
