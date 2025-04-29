package com.example.quickcommerce;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickcommerce.adapters.SavedAddressAdapter;
import com.example.quickcommerce.databinding.DialogAddAddressBinding;
import com.example.quickcommerce.databinding.FragmentAddressBinding;
import com.example.quickcommerce.models.AddressModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class addressFragment extends Fragment {

    private FragmentAddressBinding binding;
    private SavedAddressAdapter savedAddressAdapter;
    private List<AddressModel> savedAddresses = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupLocationPermissionRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        loadSavedAddresses();
        setupContinueClickListener();

        return binding.getRoot();
    }

    private void setupContinueClickListener() {
        binding.btnContinue.setOnClickListener(v -> {
            AddressModel selectedAddress = savedAddressAdapter.getSelectedAddress();

            if (selectedAddress != null) {
                Bundle bundle = new Bundle();
                bundle.putString("selected_address", selectedAddress.getAddress());
                bundle.putInt("amount", getArguments() != null ? getArguments().getInt("amount", 0) : 0);

                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_addressFragment_to_paymentFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Please select an address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching location...");
    }

    private void setupRecyclerView() {
        savedAddressAdapter = new SavedAddressAdapter(savedAddresses, position -> {
            if (position >= 0 && position < savedAddresses.size()) {
                AddressModel address = savedAddresses.get(position);
                deleteAddressFromFirebase(address.getId());
            }
        });
        binding.rvSavedAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSavedAddresses.setAdapter(savedAddressAdapter);
    }

    private void setupClickListeners() {
        binding.btnCheckPincode.setOnClickListener(v -> {
            String pincode = binding.etPincode.getText().toString().trim();
            if (TextUtils.isEmpty(pincode)) {
                Toast.makeText(getContext(), "Please enter a pincode", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Checking delivery for pincode: " + pincode, Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvUseCurrentLocation.setOnClickListener(v -> requestLocationPermission());
        binding.tvAddNewAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    private void setupLocationPermissionRequest() {
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void requestLocationPermission() {
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        progressDialog.show();

        fusedLocationProviderClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener(location -> {
            progressDialog.dismiss();
            if (location != null) {
                convertLocationToAddress(location);
            } else {
                Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void convertLocationToAddress(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                saveAddressToFirebase(address.getAddressLine(0), address.getLocality(),
                        address.getAdminArea(), address.getPostalCode(), address.getCountryName());
            } else {
                Toast.makeText(getContext(), "No address found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to get address", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogAddAddressBinding dialogBinding = DialogAddAddressBinding.inflate(LayoutInflater.from(requireContext()));
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        dialogBinding.btnSave.setOnClickListener(v -> {
            String addressLine = dialogBinding.etStreet.getText().toString().trim();
            String city = dialogBinding.etCity.getText().toString().trim();
            String state = dialogBinding.etState.getText().toString().trim();
            String postalCode = dialogBinding.etPincode.getText().toString().trim();
            String country = dialogBinding.etCountry.getText().toString().trim();

            if (TextUtils.isEmpty(addressLine) || TextUtils.isEmpty(city) ||
                    TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode) || TextUtils.isEmpty(country)) {
                Toast.makeText(getContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show();
            } else {
                saveAddressToFirebase(addressLine, city, state, postalCode, country);
                dialog.dismiss();
            }
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveAddressToFirebase(String fullAddress, String city, String state, String postalCode, String country) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference addressesRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("SavedAddresses");

        String addressId = UUID.randomUUID().toString();
        AddressModel addressModel = new AddressModel(addressId, userId, "Default Name",
                "0000000000", fullAddress, city, state, postalCode, false);

        addressesRef.child(addressId).setValue(addressModel)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address saved!", Toast.LENGTH_SHORT).show();
                    loadSavedAddresses();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save address", Toast.LENGTH_SHORT).show());
    }

    private void deleteAddressFromFirebase(String addressId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference addressesRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("SavedAddresses");

        addressesRef.child(addressId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address deleted successfully", Toast.LENGTH_SHORT).show();
                    loadSavedAddresses();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSavedAddresses() {
        savedAddresses.clear();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference addressesRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("SavedAddresses");

        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot addressSnap : snapshot.getChildren()) {
                    AddressModel address = addressSnap.getValue(AddressModel.class);
                    if (address != null) {
                        savedAddresses.add(address);
                    }
                }
                savedAddressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load addresses: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

