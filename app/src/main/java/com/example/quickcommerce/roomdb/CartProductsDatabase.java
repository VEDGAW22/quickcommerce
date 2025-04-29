package com.example.quickcommerce.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database for cart products.
 */
@Database(entities = {CartProducts.class}, version = 1, exportSchema = false)
public abstract class CartProductsDatabase extends RoomDatabase {

    // Correct DAO name
    public abstract CartDao cartDao();

    private static volatile CartProductsDatabase INSTANCE;

    /**
     * Get the singleton instance of the database.
     */
    public static CartProductsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CartProductsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CartProductsDatabase.class,
                                    "quickcommerce.db"
                            )
                            // In development only. Remove this in production and use migrations instead.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

