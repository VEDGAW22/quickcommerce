package com.example.quickcommerce;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickcommerce.AuthViewModel.OrderDetailViewModel;
import com.example.quickcommerce.adapters.AdapterOrdered;
import com.example.quickcommerce.databinding.FragmentOrderDetailBinding;
import com.example.quickcommerce.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private FragmentOrderDetailBinding binding;
    private OrderDetailViewModel viewModel;
    private AdapterOrdered productAdapter;
    private String orderId;
    private String orderStatus;
    private static final String TAG = "OrderDetailFragment";
    private TextView emptyMessage;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        setupRecyclerView();
        setupEmptyMessage();
        observeOrderDetails();
        fetchOrderDetails();
        updateStatusIndicators();
        onBackClick();

        return binding.getRoot();
    }

    // ------------------ Extracted Methods ------------------

    private void retrieveArguments() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            orderStatus = getArguments().getString("status");
            Log.d(TAG, "Received orderId: " + orderId);
            Log.d(TAG, "Received orderStatus: " + orderStatus);
        } else {
            Log.w(TAG, "Arguments are null!");
        }
    }

    private void setupRecyclerView() {
        binding.rvorderedItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        productAdapter = new AdapterOrdered(new ArrayList<>());
        binding.rvorderedItems.setAdapter(productAdapter);
    }

    private void setupEmptyMessage() {
        emptyMessage = new TextView(requireContext());
        emptyMessage.setText("No order items found");
        emptyMessage.setVisibility(View.GONE);
        binding.getRoot().addView(emptyMessage);
    }

    private void observeOrderDetails() {
        viewModel.getOrderDetail().observe(getViewLifecycleOwner(), orderDetail -> {
            if (orderDetail != null && orderDetail.getOrderedItems() != null) {
                List<OrderItem> orderItems = orderDetail.getOrderedItems();
                if (orderItems.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    Log.w(TAG, "No items found in order!");
                } else {
                    emptyMessage.setVisibility(View.GONE);
                    productAdapter.updateOrderItems(orderItems);
                    Log.d(TAG, "Order items updated in RecyclerView");
                }
            } else {
                emptyMessage.setVisibility(View.VISIBLE);
                Log.w(TAG, "Order details are null!");
            }
        });
    }

    private void fetchOrderDetails() {
        if (orderId != null) {
            Log.d(TAG, "Fetching order details for: " + orderId);
            viewModel.fetchOrderDetails(orderId);
        } else {
            Log.w(TAG, "orderId is null, skipping fetchOrderDetails()");
        }
    }

    private void updateStatusIndicators() {
        if (orderStatus == null) {
            Log.w(TAG, "orderStatus is null, cannot set status UI");
            return;
        }

        int green = getResources().getColor(R.color.green);
        // Tint based on status
        switch (orderStatus.toLowerCase()) {
            case "0":
                binding.iv1.setColorFilter(green);
                break;
            case "packed":
                binding.iv1.setColorFilter(green);
                binding.view1.setBackgroundColor(green);
                binding.iv2.setColorFilter(green);
                break;
            case "shipped":
                binding.iv1.setColorFilter(green);
                binding.view1.setBackgroundColor(green);
                binding.iv2.setColorFilter(green);
                binding.view2.setBackgroundColor(green);
                binding.iv3.setColorFilter(green);
                break;
            case "delivered":
                binding.iv1.setColorFilter(green);
                binding.view1.setBackgroundColor(green);
                binding.iv2.setColorFilter(green);
                binding.view2.setBackgroundColor(green);
                binding.iv3.setColorFilter(green);
                binding.view3.setBackgroundColor(green);
                binding.iv4.setColorFilter(green);
                break;
            default:
                Log.w(TAG, "Unknown order status: " + orderStatus);
                break;
        }
    }
    private void onBackClick(){
        binding.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
}