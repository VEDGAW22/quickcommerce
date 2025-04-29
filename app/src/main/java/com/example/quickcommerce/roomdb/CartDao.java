package com.example.quickcommerce.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insertProduct(CartProducts product);

    @Insert
    void insertAll(List<CartProducts> products);

    @Update
    void updateProduct(CartProducts product);

    @Delete
    void deleteProduct(CartProducts product);

    @Query("DELETE FROM cart")
    void clearCart();

    @Query("SELECT * FROM cart")
    List<CartProducts> getAllCartProducts();

    @Query("SELECT SUM(price * quantity) FROM cart")
    int getTotalPrice();
}
