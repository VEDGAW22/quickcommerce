package com.example.quickcommerce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickcommerce.adapters.OrderAdapter;
import com.example.quickcommerce.AuthViewModel.OrderViewModel;
import com.example.quickcommerce.databinding.FragmentMyOrderBinding;
import com.example.quickcommerce.models.MyOrderDisplay;

public class MyOrderFragment extends Fragment {

    private FragmentMyOrderBinding binding;
    private OrderAdapter orderAdapter;
    private OrderViewModel orderViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyOrderBinding.inflate(inflater, container, false);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        setupRecyclerView();

        // Fetch orders
        orderViewModel.fetchOrders();
        orderAdapter.setOnOrderClickListener(this::onOrderItemViewClicked);
        onBackClick();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter();
        binding.rvOrder.setAdapter(orderAdapter);

        orderViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), myOrderDisplays -> {
            if (myOrderDisplays != null) {
                orderAdapter.submitList(myOrderDisplays);
            }
        });
    }

    private void onOrderItemViewClicked(MyOrderDisplay order) {
        Bundle bundle = new Bundle();
        bundle.putString("status", order.getOrderStatus());
        bundle.putString("orderId", order.getOrderId());
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_myOrderFragment_to_orderDetailFragment, bundle);
    }
    private void onBackClick(){
        binding.btnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leaks
    }
}

