package com.example.quickcommerce.homepage;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.quickcommerce.Constant;
import com.example.quickcommerce.OnCategoryClickListener;
import com.example.quickcommerce.R;
import com.example.quickcommerce.adapters.AdapterBanner;
import com.example.quickcommerce.adapters.AdapterCategory;
import com.example.quickcommerce.databinding.FragmentMenBinding;
import com.example.quickcommerce.models.Category;

import java.util.ArrayList;
import java.util.Arrays;


public class MenFragment extends Fragment {
    private FragmentMenBinding binding ;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private ArrayList<Category> allCategories;
    private AdapterCategory adapterCategory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Now you can safely call this method with `view`
        handleBackPress(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMenBinding.inflate(inflater, container, false);
        prepareCategories(); // Initialize all category lists
        setBanner();         // Banner logic
        setupTabs();
        setupBottomNavigation();
        setupProfile();
        changeBar();
        return binding.getRoot();
    }

    private void setupTabs() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_menFragment_to_homeFragment);
            }
        });
        binding.tabWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_menFragment_to_womenFragment);;
            }
        });
        binding.tabKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_menFragment_to_kidsFragment);
            }
        });
    }
    private void setupProfile() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_menFragment_to_profileFragment2);
            }
        });
    }
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

            int itemId = item.getItemId(); // ✅ valid constant int from menu
            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.menFragment);
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
        for (int i = 0; i < Constant.allMenProduct.length; i++) {
            allCategories.add(new Category(Constant.allMenProduct[i], Constant.allMenProductsCategoryIcon[i]));
        }


        adapterCategory = new AdapterCategory(allCategories, new OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("category", category.getTitle());
                navController.navigate(R.id.action_menFragment_to_productFragment, bundle);
            }
        });
        binding.rvcatomen.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvcatomen.setAdapter(adapterCategory);
    }
    private void setBanner() {
        ArrayList<Integer> bannerList = new ArrayList<>(Arrays.asList(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        ));

        AdapterBanner adapter = new AdapterBanner(bannerList);
        binding.bannerMenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.bannerMenRecyclerView.setAdapter(adapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.bannerMenRecyclerView);

        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (binding != null) {
                    if (index >= bannerList.size()) index = 0;
                    binding.bannerMenRecyclerView.smoothScrollToPosition(index++);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);
    }
    private void handleBackPress(View view) {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigateUp(); // Go back to HomeFragment
            }
        });
    }

    private void changeBar() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.lightblue));
    }
}