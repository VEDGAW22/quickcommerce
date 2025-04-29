package com.example.quickcommerce;

import android.graphics.Paint;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quickcommerce.adapters.AdapterCart;
import com.example.quickcommerce.adapters.AdapterSize;
import com.example.quickcommerce.databinding.FragmentParticularProductBinding;
import com.example.quickcommerce.models.Cart;
import com.example.quickcommerce.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticularProductFragment extends Fragment {

    private FragmentParticularProductBinding binding;
    private Product product;
    private String selectedSizeInFragment = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentParticularProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the Product object passed via arguments
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
        }

        // If product is available, set up UI
        if (product != null) {
            setupProductDetails();
        }

        // Back navigation
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupProductDetails() {
        // 1) Image Slider
        List<String> imageUrls = product.getImageUrls();
        List<SlideModel> imageList = new ArrayList<>();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                imageList.add(new SlideModel(url, ScaleTypes.FIT));
            }
        }
        binding.imageSlider.setImageList(imageList);

        // 2) Title & Description
        binding.tvProductTitle.setText(product.getTitle());
        binding.tvProductDescripition.setText(product.getDescription());

        // 3) Pricing
        long mrp = product.getPrice() + 1000;
        binding.tvMrp.setText("₹" + mrp);
        binding.tvMrp.setPaintFlags(binding.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.tvPrice.setText("₹" + product.getPrice());
        binding.tvDiscount.setText("60% OFF");

        // 4) Size Options RecyclerView
        List<String> sizes = Arrays.asList("38", "40", "42", "44");
        AdapterSize sizeAdapter = new AdapterSize(sizes, selectedSize -> {
            selectedSizeInFragment = selectedSize;
        });
        binding.rvSizeOptions.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvSizeOptions.setAdapter(sizeAdapter);

        // 5) Add to Bag button
        binding.btnAddToBag.setOnClickListener(v -> {
            if (selectedSizeInFragment == null) {
                Toast.makeText(requireContext(), "Please select a size", Toast.LENGTH_SHORT).show();
                return;
            }
            Cart item = new Cart(product, selectedSizeInFragment);
            CartManager.addItem(requireContext(), item);  // <-- Pass the context here
            Toast.makeText(requireContext(),
                    "Added to cart – Size " + selectedSizeInFragment,
                    Toast.LENGTH_SHORT).show();
        });

        // 6) Buy Now button
        binding.btnBuyNow.setOnClickListener(v -> {
            if (selectedSizeInFragment == null) {
                Toast.makeText(requireContext(), "Please select a size", Toast.LENGTH_SHORT).show();
                return;
            }
            Cart item = new Cart(product, selectedSizeInFragment);
            CartManager.addItem(requireContext(), item);  // <-- Pass the context here
            NavController navController = Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_particularProductFragment_to_cartFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

