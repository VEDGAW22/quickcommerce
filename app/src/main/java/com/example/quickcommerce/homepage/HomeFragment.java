package com.example.quickcommerce.homepage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.example.quickcommerce.Constant;
import com.example.quickcommerce.OnCategoryClickListener;
import com.example.quickcommerce.R;
import com.example.quickcommerce.adapters.AdapterBanner;
import com.example.quickcommerce.adapters.AdapterCategory;
import com.example.quickcommerce.databinding.FragmentHomeBinding;
import com.example.quickcommerce.models.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private Handler bannerHandler;
    private Runnable bannerRunnable;

    private ArrayList<Category> allCategories;
    private AdapterCategory adapterCategory;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        changeBar();
        setupTabs();         // Set tab click behavior
        prepareCategories(); // Initialize all category lists
        setBanner();         // Banner logic
        setupBottomNavigation();
        setupProfile();
        navigatingToSearchFragment();
        fetchAndSetDefaultAddress();
        return binding.getRoot();
    }

    private void navigatingToSearchFragment() {
        // Getting the NavController to handle navigation
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Setting the click listener for the search CardView
        binding.searchCv.setOnClickListener(v -> {
            // Navigating to the search fragment when the CardView is clicked
            navController.navigate(R.id.action_homeFragment_to_searchFragment);
        });
    }

    private void setupTabs() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.tabMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_homeFragment_to_menFragment);
            }
        });
        binding.tabWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_homeFragment_to_womenFragment);;
            }
        });
        binding.tabKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_homeFragment_to_kidsFragment);
            }
        });
    }
    private void setupProfile() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_homeFragment_to_profileFragment2);
            }
        });
    }
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

            int itemId = item.getItemId(); // ✅ valid constant int from menu
            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.homeFragment);
            } else if (itemId == R.id.nav_cart) {
                navController.navigate(R.id.cartFragment);
            } else if (itemId == R.id.nav_order) {
                navController.navigate(R.id.myOrderFragment);
            } else if (itemId == R.id.nav_help) {
                navController.navigate(R.id.helpFragment);
            }

            return true;
        });
    }

    private void prepareCategories() {
        allCategories = new ArrayList<>();

        // Fill categories as per your logic
        for (int i = 0; i < Constant.allProduct.length; i++) {
            allCategories.add(new Category(Constant.allProduct[i], Constant.allProductsCategoryIcon[i]));
        }


        adapterCategory = new AdapterCategory(allCategories, new OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("category", category.getTitle());
                navController.navigate(R.id.action_homeFragment_to_productFragment, bundle);
            }
        });
        binding.rvcato.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvcato.setAdapter(adapterCategory);
    }


    private void setBanner() {
        ArrayList<Integer> bannerList = new ArrayList<>(Arrays.asList(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        ));

        AdapterBanner adapter = new AdapterBanner(bannerList);
        binding.bannerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.bannerRecyclerView.setAdapter(adapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.bannerRecyclerView);

        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (binding != null) {
                    if (index >= bannerList.size()) index = 0;
                    binding.bannerRecyclerView.smoothScrollToPosition(index++);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);
    }


    private void changeBar() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.yellow));
    }
    private void fetchAndSetDefaultAddress() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference addressesRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("SavedAddresses");

        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || binding == null) return;  // ✅ Prevent crash

                if (snapshot.exists()) {
                    for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                        String addressLine = addressSnapshot.child("addressLine").getValue(String.class);
                        Log.d("AddressFetch", "Fetched addressLine: " + addressLine);
                        if (addressLine != null && !addressLine.isEmpty()) {
                            binding.txtaddress.setText(addressLine);
                            return; // ✅ Set the first available address
                        }
                    }
                    Log.d("AddressFetch", "No valid addressLine found.");
                    binding.txtaddress.setText("No address found");
                } else {
                    Log.d("AddressFetch", "No address data exists.");
                    binding.txtaddress.setText("No address found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded() || binding == null) return;  // ✅ Prevent crash
                Log.e("AddressFetch", "Database error: " + error.getMessage());
                binding.txtaddress.setText("Error fetching address");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
