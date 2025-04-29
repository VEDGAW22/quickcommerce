package com.example.quickcommerce.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcommerce.R;
import com.example.quickcommerce.paymentFragment;
import com.razorpay.PaymentResultListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class UsersMainActivity extends AppCompatActivity implements PaymentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment); // ✅ use nav_host_fragment

        if (fragment != null && fragment.getChildFragmentManager().getFragments().size() > 0) {
            Fragment currentFragment = fragment.getChildFragmentManager().getFragments().get(0);
            if (currentFragment instanceof paymentFragment) {
                ((paymentFragment) currentFragment).onPaymentSuccess(razorpayPaymentID);
            }
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment); // ✅ use nav_host_fragment

        if (fragment != null && fragment.getChildFragmentManager().getFragments().size() > 0) {
            Fragment currentFragment = fragment.getChildFragmentManager().getFragments().get(0);
            if (currentFragment instanceof paymentFragment) {
                ((paymentFragment) currentFragment).onPaymentError(code, response);
            }
        }
    }
}

