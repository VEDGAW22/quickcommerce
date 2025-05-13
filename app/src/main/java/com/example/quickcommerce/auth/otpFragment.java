package com.example.quickcommerce.auth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.example.quickcommerce.AuthViewModel.AuthViewModel;
import com.example.quickcommerce.R;
import com.example.quickcommerce.activity.UsersMainActivity;
import com.example.quickcommerce.databinding.FragmentOtpBinding;
import com.example.quickcommerce.homepage.HomeFragment;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

public class otpFragment extends Fragment {

    private FragmentOtpBinding binding;
    private AuthViewModel viewModel;
    private String userNumber;
    private Dialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (getArguments() != null) {
            userNumber = getArguments().getString("number");
            if (!userNumber.startsWith("+91")) {
                userNumber = "+91" + userNumber;
            }
        }

        progressDialog = new Dialog(requireContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        changeStatusBarColor();
        setUserNumber();
        sendOTP();
        setupOtpInputs();
        setupLoginButton();
        setupBackButton();

        return view;
    }
    private void getUserNumber() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("number")) {
            userNumber = bundle.getString("number", "Unknown");
        }
        Log.d("OTPFragment", "User Number Retrieved: " + userNumber);
    }
    private void setUserNumber() {
        if (binding != null) {
            binding.tvUserNumber.setText(userNumber);
            Log.d("OTPFragment", "TextView Updated with: " + userNumber);
        } else {
            Log.e("OTPFragment", "Binding is NULL when setting user number!");
        }
    }

    private void changeStatusBarColor() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.trans));
    }

    private void sendOTP() {
        Toast.makeText(requireContext(), "Sending OTP...", Toast.LENGTH_SHORT).show();
        viewModel.sendOtp(userNumber, requireActivity());
    }

    private void setupOtpInputs() {
        EditText[] otpFields = {
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
        };

        for (int i = 0; i < otpFields.length; i++) {
            int index = i;

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && otpFields[index].getText().length() == 0 && index > 0) {
                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].setText("");
                    return true;
                }
                return false;
            });

            otpFields[i].addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(android.text.Editable s) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }
            });
        }
    }

    private void setupLoginButton() {
        binding.btnLoginContinue.setOnClickListener(v -> {
            StringBuilder otpBuilder = new StringBuilder();
            otpBuilder.append(binding.etOtp1.getText().toString());
            otpBuilder.append(binding.etOtp2.getText().toString());
            otpBuilder.append(binding.etOtp3.getText().toString());
            otpBuilder.append(binding.etOtp4.getText().toString());
            otpBuilder.append(binding.etOtp5.getText().toString());
            otpBuilder.append(binding.etOtp6.getText().toString());

            String otp = otpBuilder.toString();

            if (otp.length() != 6) {
                Toast.makeText(requireContext(), "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(viewModel.getVerificationId(), otp);
            viewModel.signInWithPhoneAuthCredential(credential, userNumber, requireActivity(), new AuthViewModel.OtpVerificationCallback() {
                @Override
                public void onVerificationSuccess() {
                    progressDialog.dismiss();
                    navigateToUserMainActivity();
                }

                @Override
                public void onVerificationFailed(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupBackButton() {
        binding.tb0tpFragment.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_otpFragment_to_signinFragment));
    }

    private void navigateToUserMainActivity() {
        Intent intent = new Intent(requireContext(), UsersMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}