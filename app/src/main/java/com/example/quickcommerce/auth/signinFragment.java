package com.example.quickcommerce.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.quickcommerce.R;
import com.example.quickcommerce.databinding.FragmentSigninBinding;
import com.example.quickcommerce.utils;

public class signinFragment extends Fragment {
    private FragmentSigninBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(binding != null){
            changeBar();
            getUserNumber();
            onContinueClickButton();
        }
    }

    private void getUserNumber() {
        binding.etUserNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                if (len == 10) {
                    binding.btnContinue.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.yellow));
                } else {
                    binding.btnContinue.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightbluegray));
                }
                binding.btnContinue.invalidate(); // Force redraw
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void onContinueClickButton() {
        binding.btnContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String number = binding.etUserNumber.getText().toString().trim();

                if (number.isEmpty() || number.length() != 10) {
                    utils.showToast(requireContext(), "Please Enter The Valid Phone Number");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("number", number);
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_signinFragment_to_otpFragment, bundle); // Direct navigation
                }
            }
        });
    }
    private void changeBar() {
        Window window = requireActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(getResources().getColor(R.color.yellow));
    }
}