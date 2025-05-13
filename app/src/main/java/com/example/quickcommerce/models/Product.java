package com.example.quickcommerce.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    private String id;
    private String title;
    private String description;
    private long price;
    private String category;
    private String gender;
    private long stock;
    private String size;
    private String type;
    private ArrayList<String> imageUrls;

    public Product() {
        // Required empty constructor for Firebase
    }

    public Product(String id, String title, String description, long price, String category, String gender,
                   long stock, String size, String type, ArrayList<String> imageUrls) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.gender = gender;
        this.stock = stock;
        this.size = size;
        this.type = type;
        this.imageUrls = imageUrls;
    }

    // New constructor to match simplified needs
    public Product(String id, String title, long price, ArrayList<String> imageUrls) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imageUrls = imageUrls;

        // Default values for other fields
        this.description = "";
        this.category = "";
        this.gender = "";
        this.stock = 0;
        this.size = "";
        this.type = "";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public long getStock() { return stock; }
    public void setStock(long stock) { this.stock = stock; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public ArrayList<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(ArrayList<String> imageUrls) { this.imageUrls = imageUrls; }

    // Equals and hashCode (for DiffUtil or comparisons)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
