package com.example.quickcommerce.AuthViewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.Manifest;
import com.example.quickcommerce.models.AddressModel;
import com.example.quickcommerce.models.Product;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserViewModel extends ViewModel {

    // LiveData for Address and Products
    private final MutableLiveData<List<Product>> productListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> filteredProductListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AddressModel>> addressListLiveData = new MutableLiveData<>();
    private List<Product> cachedProducts = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private final DatabaseReference databaseReference;
    private MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

    private final String userId;

    public UserViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        fetchAddresses();
    }

    // ************************ Address Management ************************ //


    public void fetchAddresses() {
        if (userId == null) return;

        databaseReference.child(userId).child("Addresses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<AddressModel> addressList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                AddressModel address = addressSnapshot.getValue(AddressModel.class);
                                if (address != null) {
                                    addressList.add(address);
                                }
                            }
                        }
                        addressListLiveData.setValue(addressList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserViewModel", "Failed to fetch addresses", error.toException());
                    }
                });
    }


    // ************************ Product Management ************************ //

    public LiveData<List<Product>> getProductListLiveData() {
        return productListLiveData;
    }

    public LiveData<List<Product>> getFilteredProductListLiveData() {
        return filteredProductListLiveData;
    }

    public void fetchAllProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("AllProducts");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> allProducts = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            allProducts.add(product);
                        }
                    }
                }
                cachedProducts = allProducts;
                productListLiveData.setValue(allProducts);
                filteredProductListLiveData.setValue(allProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserViewModel", "Error fetching products from AllProducts", error.toException());
                productListLiveData.setValue(null);
            }
        });
    }

    public void filterProducts(String query) {
        if (query == null || query.isEmpty()) {
            filteredProductListLiveData.setValue(allProducts);
        } else {
            List<Product> filteredList = new ArrayList<>();
            for (Product product : allProducts) {
                if (product.getTitle() != null && product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            filteredProductListLiveData.setValue(filteredList);
        }
    }

    public void updateProductStock(String productId, long quantityPurchased) {
        DatabaseReference productRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("AllProducts")
                .child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        long currentStock = product.getStock();
                        long updatedStock = currentStock - quantityPurchased;

                        if (updatedStock <= 0) {
                            productRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("UserViewModel", "Product removed as stock reached zero");
                                } else {
                                    Log.e("UserViewModel", "Failed to remove product", task.getException());
                                }
                            });
                        } else {
                            productRef.child("stock").setValue(updatedStock).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("UserViewModel", "Stock updated successfully");
                                } else {
                                    Log.e("UserViewModel", "Failed to update stock", task.getException());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserViewModel", "Database error while updating stock", error.toException());
            }
        });
    }


    public void fetchProductsByCategory(String category) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(category);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> categoryProducts = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            categoryProducts.add(product);
                        }
                    }
                }
                productListLiveData.setValue(categoryProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminViewModel", "Error fetching products from category " + category, error.toException());
                productListLiveData.setValue(null);
            }
        });
    }
    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
    }

}
