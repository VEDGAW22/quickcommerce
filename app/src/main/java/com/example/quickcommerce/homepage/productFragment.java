package com.example.quickcommerce.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quickcommerce.AuthViewModel.UserViewModel;
import com.example.quickcommerce.R;
import com.example.quickcommerce.adapters.AdapterProduct;
import com.example.quickcommerce.databinding.FragmentProductBinding;
import com.example.quickcommerce.models.Product;

import java.util.ArrayList;

public class productFragment extends Fragment {

    private FragmentProductBinding binding;
    private String category = null;
    private AdapterProduct adapterProduct;
    private UserViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout and initialize ViewModel
        binding = FragmentProductBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Setup core UI
        setupRecyclerView();
        setupToolbar();
        getProductCategory();
        observeProductList();

        // Load product data
        fetchCategoryProducts();
        handleSearchMenuClick();
        handleBackClick();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapterProduct = new AdapterProduct();
        adapterProduct.setOnProductClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", product);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_productFragment_to_particularProductFragment, bundle);
        });

        binding.rvProduct.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProduct.setAdapter(adapterProduct);
    }

    private void setupToolbar() {
        binding.tbSeaarchFragment.setTitle(category);
    }

    private void getProductCategory() {
        category = getArguments() != null ? getArguments().getString("category") : "Products";
    }

    private void observeProductList() {
        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), products -> {
            binding.shimmerViewConntainer.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                binding.rvProduct.setVisibility(View.VISIBLE);
                binding.tvText.setVisibility(View.GONE);
                adapterProduct.submitProductList(products);
            } else {
                binding.rvProduct.setVisibility(View.GONE);
                binding.tvText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchCategoryProducts() {
        binding.shimmerViewConntainer.setVisibility(View.VISIBLE);
        binding.rvProduct.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.GONE);
        viewModel.fetchProductsByCategory(category);
    }


    private void handleSearchMenuClick() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.tbSeaarchFragment.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                navController.navigate(R.id.action_productFragment_to_searchFragment);
                return true;
            }
        });
    }

    private void handleBackClick() {
        binding.backArrowIv.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}
