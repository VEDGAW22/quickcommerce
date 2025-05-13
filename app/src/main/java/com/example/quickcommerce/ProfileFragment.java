package com.example.quickcommerce;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickcommerce.AuthViewModel.UserViewModel;
import com.example.quickcommerce.activity.AuthMainActivity;
import com.example.quickcommerce.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AlertDialog.Builder AlertDialog;
    private UserViewModel viewmodle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        viewmodle = new ViewModelProvider(this).get(UserViewModel.class);
        onBackclick();
        suport();
        fetchUserPhoneNumber();
        yourOrders();
        address();
        onLogOut();
        return binding.getRoot();
    }

    private void onLogOut() {
        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Log Out")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Perform logout
                            if (viewmodle!= null) {
                                viewmodle.logoutUser(); // Your custom logout logic
                            } else {
                                FirebaseAuth.getInstance().signOut(); // Fallback
                            }

                            // Navigate to AuthMainActivity
                            Intent intent = new Intent(requireContext(), AuthMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
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
                navController.navigate(R.id.action_profileFragment_to_myOrderFragment);
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
    private void fetchUserPhoneNumber() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("phone");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phoneNumber = snapshot.getValue(String.class);
                    Log.d("PhoneFetch", "Phone number: " + phoneNumber);

                    // Example: set it to a TextView
                    binding.txtAccHolderNumber.setText(phoneNumber);
                } else {
                    Log.d("PhoneFetch", "No phone number found.");
                    binding.txtAccHolderNumber.setText("No phone available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PhoneFetch", "Database error: " + error.getMessage());
                binding.txtAccHolderNumber.setText("Error fetching phone");
            }
        });
    }

}
