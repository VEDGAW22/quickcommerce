package com.example.quickcommerce.AuthViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;

import com.example.quickcommerce.models.OrderDetail;
import com.example.quickcommerce.models.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailViewModel extends ViewModel {

    private final MutableLiveData<OrderDetail> orderDetailLiveData = new MutableLiveData<>();
    private final DatabaseReference databaseReference;
    private static final String TAG = "OrderDetailViewModel";

    public OrderDetailViewModel() {
        // ✅ Corrected path
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Admins")
                .child("Orders");
    }

    public LiveData<OrderDetail> getOrderDetail() {
        return orderDetailLiveData;
    }

    public void fetchOrderDetails(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "Invalid orderId: " + orderId);
            orderDetailLiveData.postValue(null);
            return;
        }

        Log.d(TAG, "Fetching order details for ID: " + orderId);

        DatabaseReference orderRef = databaseReference.child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "No order found for ID: " + orderId);
                    orderDetailLiveData.postValue(null);
                    return;
                }

                Log.d(TAG, "Order data retrieved: " + dataSnapshot.getValue());

                String orderStatus = String.valueOf(dataSnapshot.child("orderStatus").getValue());
                Double totalAmount = dataSnapshot.child("totalAmount").getValue(Double.class);
                if (totalAmount == null) totalAmount = 0.0;

                List<OrderItem> orderItems = new ArrayList<>();

                for (DataSnapshot itemSnapshot : dataSnapshot.child("cartItems").getChildren()) {
                    String productId = itemSnapshot.child("productId").getValue(String.class);
                    String title = itemSnapshot.child("title").getValue(String.class);
                    String description = itemSnapshot.child("description").getValue(String.class);
                    String size = itemSnapshot.child("size").getValue(String.class);
                    String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                    Double price = itemSnapshot.child("price").getValue(Double.class);
                    Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);

                    if (productId == null || productId.isEmpty()) {
                        Log.w(TAG, "Skipping item due to missing productId");
                        continue;
                    }

                    orderItems.add(new OrderItem(
                            productId,
                            title != null ? title : "Unknown",
                            size != null ? size : "",
                            quantity != null ? quantity : 1,
                            price != null ? price : 0.0,
                            imageUrl != null ? imageUrl : ""
                    ));
                }

                OrderDetail orderDetail = new OrderDetail(
                        orderId,
                        orderStatus != null ? orderStatus : "Unknown",
                        orderItems,
                        totalAmount
                );

                orderDetailLiveData.postValue(orderDetail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                orderDetailLiveData.postValue(null);
            }
        });
    }

}

