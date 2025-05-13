package com.example.quickcommerce.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.quickcommerce.AuthViewModel.AuthViewModel;
import com.example.quickcommerce.R;
import com.example.quickcommerce.activity.UsersMainActivity;
import com.example.quickcommerce.databinding.FragmentSplashBinding;
import com.example.quickcommerce.homepage.HomeFragment;


public class SplashFragment extends Fragment {

    private AuthViewModel viewModel;
    private FragmentSplashBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        // Use requireActivity() to get the ViewModel that persists across Fragments within the same Activity
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        changeBar();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeLoginStatus();
    }

    private void observeLoginStatus() {
        // checkIfAlreadyLoggedIn in ViewModel will set isCurrentUser
        viewModel.checkIfAlreadyLoggedIn();

        viewModel.isCurrentUser.observe(getViewLifecycleOwner(), isLoggedIn -> {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (binding != null) {
                    if (isLoggedIn != null && isLoggedIn) {
                        Intent intent = new Intent(requireContext(), UsersMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_splashFragment_to_signinFragment);
                    }
                }
            }, isLoggedIn != null && isLoggedIn ? 800 : 3000); // Short delay if logged in, longer otherwise
        });
    }

    private void changeBar() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.trans));
    }
}