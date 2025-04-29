package com.example.quickcommerce.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class CartProducts {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String product_id;
    private String title;
    private int price;
    private String size;
    private int quantity;
    private String image_url;

    // --- Constructor ---
    public CartProducts(String product_id, String title, int price, String size, int quantity, String image_url) {
        this.product_id = product_id;
        this.title = title;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.image_url = image_url;
    }

    // --- Getters and Setters ---
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
