package com.example.quickcommerce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quickcommerce.databinding.FragmentHelpBinding;


public class helpFragment extends Fragment {
    private FragmentHelpBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(inflater,container,false);
        onBackClick();
        setupEmailSupport();
        setupPhoneSupport();
        return binding.getRoot();
    }
    private void onBackClick(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }
    private void setupEmailSupport() {
        binding.emailLayout.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@quickcommerce.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help & Support");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I need help with...");

            if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPhoneSupport() {
        binding.phoneLayout.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+918208595070"));

            if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(callIntent);
            } else {
                Toast.makeText(requireContext(), "No dialer app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

}