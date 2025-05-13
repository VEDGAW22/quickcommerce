package com.example.quickcommerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.quickcommerce.adapters.AdapterCart;
import com.example.quickcommerce.databinding.FragmentCartBinding;
import com.example.quickcommerce.models.Cart;

import java.util.List;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private AdapterCart cartAdapter;
    private List<Cart> cartItems;
    private int subtotal = 0; // To store total cart amount

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartItems = CartManager.getCartItems(requireContext());
        cartAdapter = new AdapterCart(this::onItemRemoved);
        binding.checkrecycle.setAdapter(cartAdapter);

        // Submit the cart items to the adapter
        cartAdapter.submitList(cartItems);

        updateBillDetails();
        updateCartUI();
        setupPlaceOrderButton();

        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    // Setup Place Order Button
    private void setupPlaceOrderButton() {
        binding.placeOrderButton.setOnClickListener(v -> {
            if (subtotal > 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("amount", subtotal); // Passing cart amount to next fragment
                // If you also want to pass cartItems list, you can serialize and send it later.

                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_cartFragment_to_addressFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBillDetails() {
        subtotal = 0;
        for (Cart cart : cartItems) {
            subtotal += cart.getProduct().getPrice();
        }

        binding.subTotalAmount.setText("₹" + subtotal);
        binding.deliveryChargeAmount.setText("Free");
        binding.grandTotalAmount.setText("₹" + subtotal);
    }

    private void onItemRemoved(int position) {
        cartItems.remove(position);
        CartManager.removeItem(requireContext(), position);
        cartAdapter.notifyItemRemoved(position);
        updateCartUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateCartUI() {
        if (cartItems.isEmpty()) {
            binding.billcard.setVisibility(View.GONE);
            binding.paymentBar.setVisibility(View.GONE);
        } else {
            binding.billcard.setVisibility(View.VISIBLE);
            binding.paymentBar.setVisibility(View.VISIBLE);
            updateBillDetails();
        }
    }
}




