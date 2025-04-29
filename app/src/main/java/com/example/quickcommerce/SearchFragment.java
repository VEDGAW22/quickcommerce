package com.example.quickcommerce;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quickcommerce.AuthViewModel.UserViewModel;
import com.example.quickcommerce.adapters.AdapterProduct;
import com.example.quickcommerce.databinding.FragmentSearchBinding;
import com.example.quickcommerce.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private AdapterProduct adapterProduct;
    private UserViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        fetchProducts();
        setupProductRecycler();
        observeProductList();
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapterProduct != null) {
                    adapterProduct.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        handleBackPress();


        return binding.getRoot();
    }

    private void fetchProducts() {
        binding.shimmerViewConntainer.setVisibility(View.VISIBLE);
        binding.rvProduct.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.GONE);

        // Call the correct method to fetch products by category
        viewModel.fetchAllProducts();
    }

    private void observeProductList() {
        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), products -> {
            binding.shimmerViewConntainer.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                binding.rvProduct.setVisibility(View.VISIBLE);
                binding.tvText.setVisibility(View.GONE);
                adapterProduct.submitProductList(products); // ✅ Use this
            } else {
                binding.rvProduct.setVisibility(View.GONE);
                binding.tvText.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setupProductRecycler() {
        adapterProduct = new AdapterProduct();
        binding.rvProduct.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProduct.setAdapter(adapterProduct);
    }

    private void handleBackPress() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.backArrowIv.setOnClickListener(v ->
                navController.navigate(R.id.action_searchFragment_to_homeFragment));


        binding.searchEt.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(binding.searchEt.getWindowToken(), 0);
                }
                binding.searchEt.clearFocus();
                return true;
            }
            return false;
        });
    }

}

