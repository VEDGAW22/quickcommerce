package com.example.quickcommerce.models;

import java.io.Serializable;

public class Cart implements Serializable {
    private Product product;
    private String selectedSize;
    private int quantity;  // Add quantity field

    public Cart(Product product, String selectedSize) {
        this.product = product;
        this.selectedSize = selectedSize;
    }

    // Getters and setters
    public Product getProduct() {
        return product;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Optional: Safely get product ID
    public String getProductId() {
        return product != null ? product.getId() : null;
    }
}
