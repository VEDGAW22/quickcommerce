package com.example.quickcommerce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickcommerce.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        onBackclick();
        suport();
        yourOrders();
        address();
        return binding.getRoot();
    }
    private void onBackclick (){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
    private void suport (){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_helpFragment);
            }
        });
    }
    private void yourOrders (){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.yourOrdersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_myOrder);
            }
        });
    }
    private void address (){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_addressFragment);
            }
        });
    }
}
