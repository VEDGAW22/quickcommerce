package com.example.quickcommerce;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.quickcommerce.models.Cart;
import com.example.quickcommerce.models.CartOrder;
import com.example.quickcommerce.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class paymentFragment extends Fragment implements PaymentResultListener {

    private Button btnPayNow;
    private LinearLayout optionUPI, optionCOD;
    private int amount = 0;
    private List<Cart> cartItems;
    private String selectedPaymentMethod = ""; // "UPI" or "COD"
    private ImageView btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);

        Checkout.preload(requireContext());

        btnPayNow = root.findViewById(R.id.btnPayNow);
        optionUPI = root.findViewById(R.id.optionUPI);
        optionCOD = root.findViewById(R.id.optionCOD);
        btnBack = root.findViewById(R.id.btnBack);

        // Get amount passed from previous fragment
        if (getArguments() != null) {
            amount = getArguments().getInt("amount", 0);
        }

        cartItems = CartManager.getCartItems(requireContext());

        // Handle UPI option click
        optionUPI.setOnClickListener(v -> {
            selectedPaymentMethod = "UPI";
            highlightSelectedOption();
        });

        // Handle COD option click
        optionCOD.setOnClickListener(v -> {
            selectedPaymentMethod = "COD";
            highlightSelectedOption();
        });

        // Handle Pay Now button click
        btnPayNow.setOnClickListener(v -> {
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else if (selectedPaymentMethod.equals("UPI")) {
                if (amount > 0) {
                    initiateRazorpayPayment(amount);
                } else {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            } else if (selectedPaymentMethod.equals("COD")) {
                placeOrderWithoutPayment();
            }
        });
        OnBackPressed();

        return root;
    }

    private void highlightSelectedOption() {
        // Reset background
        optionUPI.setBackgroundResource(R.drawable.payment_option_bg);
        optionCOD.setBackgroundResource(R.drawable.payment_option_bg);

        // Highlight selected option
        if (selectedPaymentMethod.equals("UPI")) {
            optionUPI.setBackgroundColor(Color.parseColor("#FFF9C4")); // Light Yellow
        } else if (selectedPaymentMethod.equals("COD")) {
            optionCOD.setBackgroundColor(Color.parseColor("#FFF9C4")); // Light Yellow
        }
    }

    private void initiateRazorpayPayment(int amount) {
        final Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_OiZ7uvxTgB1q8t"); // Your Razorpay Key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "QuickCommerce");
            options.put("description", "Order Payment");
            options.put("currency", "INR");
            options.put("amount", amount * 100); // Razorpay uses paise
            options.put("prefill.email", "test@example.com");
            options.put("prefill.contact", "9876543210");

            checkout.open(requireActivity(), options);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Payment error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(getContext(), "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        placeOrderAfterUpiPayment();
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(getContext(), "Payment Failed: " + response, Toast.LENGTH_SHORT).show();
    }

    private void placeOrderAfterUpiPayment() {
        createAndSaveOrder();
        Toast.makeText(getContext(), "Order placed successfully (Online Payment)", Toast.LENGTH_SHORT).show();
    }

    private void placeOrderWithoutPayment() {
        createAndSaveOrder();
        Toast.makeText(getContext(), "Order placed successfully (Cash On Delivery)", Toast.LENGTH_SHORT).show();
    }

    private void createAndSaveOrder() {
        List<CartOrder.CartItem> cartItemList = convertCartToCartItems(cartItems);
        String orderId = FirebaseDatabase.getInstance().getReference().push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        double totalAmount = 0;
        for (CartOrder.CartItem cartItem : cartItemList) {
            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
        }

        CartOrder.Order order = new CartOrder.Order(orderId, userId, cartItemList, totalAmount);

        saveOrderToFirebase(order);
        CartManager.clearCart(requireContext());
    }

    private List<CartOrder.CartItem> convertCartToCartItems(List<Cart> cartItems) {
        List<CartOrder.CartItem> cartItemList = new ArrayList<>();
        for (Cart cart : cartItems) {
            Product product = cart.getProduct();
            int quantity = 1;
            cartItemList.add(new CartOrder.CartItem(
                    product.getId(),
                    product.getTitle(),
                    quantity,
                    product.getPrice()
            ));
        }
        return cartItemList;
    }

    private void saveOrderToFirebase(CartOrder.Order order) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AllUser").child("Users");
        databaseReference.child(userId).child("Orders").child(order.getOrderId()).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseOrder", "Order saved successfully");
                    } else {
                        Log.e("FirebaseOrder", "Failed to save order");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseOrder", "Error: " + e.getMessage(), e);
                });
    }
    private void OnBackPressed() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
}

