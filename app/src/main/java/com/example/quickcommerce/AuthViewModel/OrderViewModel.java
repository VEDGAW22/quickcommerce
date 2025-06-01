package com.example.quickcommerce.AuthViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quickcommerce.models.CartOrder;
import com.example.quickcommerce.models.MyOrderDisplay;
import com.example.quickcommerce.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<MyOrderDisplay>> ordersLiveData = new MutableLiveData<>();

    public LiveData<List<MyOrderDisplay>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void fetchOrders() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();; // Replace with actual user ID

        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("Orders");

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<MyOrderDisplay> displayList = new ArrayList<>();

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    CartOrder.Order order = orderSnap.getValue(CartOrder.Order.class);

                    if (order != null && currentUserId.equals(order.getUserId())) {
                        List<String> imageUrls = new ArrayList<>();
                        List<CartOrder.CartItem> cartItems = order.getCartItems();

                        if (cartItems != null) {
                            for (CartOrder.CartItem item : cartItems) {
                                if (item.getImageUrl() != null) {
                                    imageUrls.add(item.getImageUrl());
                                }
                            }
                        }

                        String statusText = getOrderStatusText(order.getOrderStatus());

                        MyOrderDisplay display = new MyOrderDisplay(
                                order.getOrderId(),
                                imageUrls,
                                statusText,
                                order.getOrderDate(),
                                "₹" + String.format("%.2f", order.getTotalAmount()),
                                4.5f,
                                cartItems
                        );

                        displayList.add(display);
                    }
                }

                ordersLiveData.setValue(displayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("OrderViewModel", "Database error: " + error.getMessage());
            }
        });
    }

    // Use int-based status
    public String getOrderStatusText(long status) {
        if (status == 0) {
            return "Ordered";
        } else if (status == 1) {
            return "Shipped";
        } else if (status == 2) {
            return "Out for Delivery";
        } else if (status == 3) {
            return "Delivered";
        }
        return "Ordered"; // Default to "Ordered" if an invalid status is given
    }
}
