package com.example.quickcommerce;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.quickcommerce.models.Cart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "cart_pref";
    private static final String CART_KEY = "cart_items";

    public static void addItem(Context context, Cart item) {
        List<Cart> cartItems = getCartItems(context);
        cartItems.add(item);
        saveCartItems(context, cartItems);
    }

    public static void removeItem(Context context, int position) {
        List<Cart> cartItems = getCartItems(context);
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            saveCartItems(context, cartItems);
        }
    }

    public static void clearCart(Context context) {
        saveCartItems(context, new ArrayList<>());
    }

    public static List<Cart> getCartItems(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Cart>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private static void saveCartItems(Context context, List<Cart> cartItems) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(cartItems);
        editor.putString(CART_KEY, json);
        editor.apply();
    }
}


