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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.quickcommerce.AuthViewModel.UserViewModel;
import com.example.quickcommerce.adapters.AdapterCart;
import com.example.quickcommerce.models.Cart;
import com.example.quickcommerce.models.CartOrder;
import com.example.quickcommerce.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class paymentFragment extends Fragment implements PaymentResultListener {

    private Button btnPayNow;
    private LinearLayout optionUPI, optionCOD;
    private ImageView btnBack;
    private int amount = 0;
    private String selectedPaymentMethod = "";
    private List<Cart> cartItems;
    private AdapterCart cartAdapter;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        initializeViews(root);
        setupListeners();

        Checkout.preload(requireContext());

        if (getArguments() != null) {
            amount = getArguments().getInt("amount", 0);
        }

        cartItems = CartManager.getCartItems(requireContext());

        return root;
    }

    private void initializeViews(View root) {
        btnPayNow = root.findViewById(R.id.btnPayNow);
        optionUPI = root.findViewById(R.id.optionUPI);
        optionCOD = root.findViewById(R.id.optionCOD);
        btnBack = root.findViewById(R.id.btnBack);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupListeners() {
        optionUPI.setOnClickListener(v -> {
            selectedPaymentMethod = "UPI";
            highlightSelectedOption();
        });

        optionCOD.setOnClickListener(v -> {
            selectedPaymentMethod = "COD";
            highlightSelectedOption();
        });

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

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void highlightSelectedOption() {
        optionUPI.setBackgroundResource(R.drawable.payment_option_bg);
        optionCOD.setBackgroundResource(R.drawable.payment_option_bg);

        int highlightColor = Color.parseColor("#FFF9C4");
        if ("UPI".equals(selectedPaymentMethod)) {
            optionUPI.setBackgroundColor(highlightColor);
        } else if ("COD".equals(selectedPaymentMethod)) {
            optionCOD.setBackgroundColor(highlightColor);
        }
    }

    private void initiateRazorpayPayment(int amount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_OiZ7uvxTgB1q8t"); // Razorpay test key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "QuickCommerce");
            options.put("description", "Order Payment");
            options.put("currency", "INR");
            options.put("amount", amount * 100); // in paise
            options.put("prefill.email", "test@example.com");
            options.put("prefill.contact", "9876543210");

            checkout.open(requireActivity(), options);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Payment error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(getContext(), "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        placeOrderAfterUpiPayment();
        navigateToOrders();
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
        navigateToOrders();
    }

    private void navigateToOrders() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_paymentFragment_to_myOrderFragment);
    }

    private List<CartOrder.CartItem> convertCartToCartItems(List<Cart> cartItems) {
        List<CartOrder.CartItem> cartItemList = new ArrayList<>();
        for (Cart cart : cartItems) {
            Product product = cart.getProduct();
            int quantity = 1;  // Assuming quantity of 1 per cart item

            // Get the first image URL from the list of imageUrls or an empty string if the list is empty
            String imageUrl = (product.getImageUrls() != null && !product.getImageUrls().isEmpty())
                    ? product.getImageUrls().get(0)
                    : ""; // Default to empty string if no images available

            // Create CartItem with the first image URL
            cartItemList.add(new CartOrder.CartItem(
                    product.getId(),
                    product.getTitle(),
                    quantity,
                    product.getPrice(),
                    imageUrl // Pass the first image URL here
            ));
        }
        return cartItemList;
    }



    private void createAndSaveOrder() {
        List<CartOrder.CartItem> cartItemList = convertCartToCartItems(cartItems);
        String orderId = FirebaseDatabase.getInstance().getReference().push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        double totalAmount = 0;

        for (int i = 0; i < cartItems.size(); i++) {
            Cart cart = cartItems.get(i);
            Product product = cart.getProduct();
            int quantity = 1;

            totalAmount += product.getPrice();

            userViewModel.updateProductStock(product.getId(), quantity);

            if (product.getStock() - quantity <= 0) {
                removeProductFromDatabase(product.getId());
                cartItems.remove(i);
                i--;
            }
        }

        CartOrder.Order order = new CartOrder.Order(orderId, userId, cartItemList, totalAmount, 0, "default");
        saveOrderToFirebase(order);

        CartManager.clearCart(requireContext());
        cartItems.clear();
        if (cartAdapter != null) cartAdapter.notifyDataSetChanged();
    }

    private void saveOrderToFirebase(CartOrder.Order order) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            String generatedOrderId = FirebaseDatabase.getInstance()
                    .getReference("Admins")
                    .child("Orders")
                    .push()
                    .getKey();
            order.setOrderId(generatedOrderId);
        }

        order.setUserId(userId);
        order.setOrderStatus(0);  // 0 = Placed
        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("Orders")
                .child(order.getOrderId())
                .setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseOrder", "Order saved successfully.");
                    } else {
                        Log.e("FirebaseOrder", "Failed to save order.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseOrder", "Error: " + e.getMessage()));
    }

    private void removeProductFromDatabase(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Admins/ProductCategory");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        if (productSnapshot.getKey().equals(productId)) {
                            productSnapshot.getRef().removeValue();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to remove product: " + error.getMessage());
            }
        });
    }
}
