package com.example.quickcommerce.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.quickcommerce.AuthViewModel.AuthViewModel;
import com.example.quickcommerce.R;
import com.example.quickcommerce.activity.UsersMainActivity;
import com.example.quickcommerce.databinding.FragmentOtpBinding;
import com.example.quickcommerce.utils;
import com.google.android.material.textfield.TextInputEditText;

public class otpFragment extends Fragment {
    private FragmentOtpBinding binding;
    private AuthViewModel authViewModel;
    private String userNumber = "Unknown"; // Default value

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBar();
        getUserNumber(); // Retrieve user number from the bundle.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUserNumber();
        customizingEnteringOtp();
        sendOtp();
        onLoginClickButton();
        onBackClick();
    }

    private void onLoginClickButton() {
        binding.btnLoginContinue.setOnClickListener(v -> {
            utils.showDialog(requireContext(), "Signing you in...");

            TextInputEditText[] otpFields = new TextInputEditText[]{
                    binding.etOtp1, binding.etOtp2, binding.etOtp3,
                    binding.etOtp4, binding.etOtp5, binding.etOtp6
            };

            // Build the OTP string from all input fields.
            StringBuilder otpBuilder = new StringBuilder();
            for (TextInputEditText field : otpFields) {
                if (field.getText() != null) {
                    otpBuilder.append(field.getText().toString());
                }
            }
            String otp = otpBuilder.toString().trim();

            if (otp.length() < otpFields.length) {
                utils.showToast(requireContext(), "Please enter the complete OTP");
                utils.hideDialog();
            } else {
                // Verify the OTP.
                verifyOtp(otp);
            }
        });
    }

    private void verifyOtp(String otp) {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.verifyOtp(otp, userNumber, requireActivity(), new AuthViewModel.OtpVerificationCallback() {
            @Override
            public void onVerificationSuccess() {
                utils.hideDialog();
                utils.showToast(requireContext(), "Logged In Successfully");
                // Navigate to the next screen if needed.
                Intent intent = new Intent(requireActivity(), UsersMainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(String errorMessage) {
                utils.hideDialog();
                utils.showToast(requireContext(), "Verification failed: " + errorMessage);
            }
        });
    }

    private void sendOtp() {
        utils.showDialog(requireContext(), "Sending OTP...");
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.sendOTP(userNumber, requireActivity(),false);
        utils.hideDialog();
        utils.showToast(requireContext(), "OTP Sent");
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

    private void customizingEnteringOtp() {
        TextInputEditText[] otpFields = new TextInputEditText[]{
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
        };

        for (TextInputEditText field : otpFields) {
            field.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)}); // Limit to 1 char
        }

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;

            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }

                    // Change button color when all digits are filled
                    boolean allFilled = true;
                    for (TextInputEditText field : otpFields) {
                        if (field.getText() == null || field.getText().toString().trim().isEmpty()) {
                            allFilled = false;
                            break;
                        }
                    }

                    if (allFilled) {
                        binding.btnLoginContinue.setBackgroundColor(getResources().getColor(R.color.yellow)); // Replace with your enabled color
                        binding.btnLoginContinue.setEnabled(true);
                    } else {
                        binding.btnLoginContinue.setBackgroundColor(getResources().getColor(R.color.lightbluegray)); // Replace with your disabled color
                        binding.btnLoginContinue.setEnabled(false);
                    }
                }
            });
        }
    }


    private void onBackClick() {
        binding.tb0tpFragment.setNavigationOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_otpFragment_to_signinFragment);
        });
    }
    private void changeBar() {
        Window window = requireActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(getResources().getColor(R.color.yellow));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks.
    }
}
