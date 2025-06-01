package com.example.quickcommerce.AuthViewModel;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.quickcommerce.models.Users;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class AuthViewModel extends ViewModel {
    private FirebaseAuth mAuth;
    private final MutableLiveData<Boolean> _otpSent = new MutableLiveData<>(false);
    private final LiveData<Boolean> otpSent = _otpSent;
    private final MutableLiveData<Boolean> _paymentStatus = new MutableLiveData<>(false);
    private final LiveData<Boolean> paymentStatus = _paymentStatus;
    private String _verificationId = "";
    private final MutableLiveData<Boolean> _isSignedInSuccessfully = new MutableLiveData<>(false);
    private final LiveData<Boolean> isSignedInSuccessfully = _isSignedInSuccessfully;
    private final MutableLiveData<Boolean> _isCurrentUser = new MutableLiveData<>(false);
    public final LiveData<Boolean> isCurrentUser = _isCurrentUser;
    private final MutableLiveData<String> _userAddress = new MutableLiveData<>();
    public final LiveData<String> userAddress = _userAddress;

    public AuthViewModel() {
        mAuth = FirebaseAuth.getInstance();
        checkIfAlreadyLoggedIn();
    }

    public LiveData<Boolean> getOtpSent() {
        return otpSent;
    }

    public LiveData<Boolean> getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean value) {
        _paymentStatus.setValue(value);
    }

    public LiveData<Boolean> getIsSignedInSuccessfully() {
        return isSignedInSuccessfully;
    }

    public LiveData<Boolean> getIsCurrentUser() {
        return isCurrentUser;
    }

    public String getVerificationId() {
        return _verificationId;
    }

    public void sendOtp(String number, Activity activity) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "Verification completed: " + credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "Verification failed", e);
                _otpSent.setValue(false);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                _verificationId = verificationId;
                _otpSent.setValue(true);
                Log.d(TAG, "OTP Sent Successfully: " + verificationId);
            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setActivity(activity).setCallbacks(mCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String phoneNumber, Activity activity, OtpVerificationCallback callback) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, (Task<AuthResult> task) -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential: success");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                        if (tokenTask.isSuccessful()) {
                            String token = tokenTask.getResult();
                            Users user = new Users(userId, phoneNumber, null,null);
                            user.setUserToken(token); // ✅ Set FCM token here

                            FirebaseDatabase.getInstance().getReference("AllUser").child("Users").child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Log.d(TAG, "User saved in database successfully");
                                    saveLoginState(true);
                                    if (callback != null) {
                                        callback.onVerificationSuccess();
                                    }
                                } else {
                                    Log.e(TAG, "Failed to save user in database", dbTask.getException());
                                    if (callback != null) {
                                        callback.onVerificationFailed("Failed to save user in database.");
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG, "Failed to get FCM token", tokenTask.getException());
                            if (callback != null) {
                                callback.onVerificationFailed("Failed to get FCM token.");
                            }
                        }
                    });
                }
            } else {
                Log.e(TAG, "signInWithCredential: failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "Invalid verification code.");
                }
                if (callback != null) {
                    callback.onVerificationFailed("Invalid OTP. Please try again.");
                }
            }
        });
    }


    public void checkIfAlreadyLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        _isCurrentUser.setValue(currentUser != null);
        if (currentUser != null) {
            Log.d(TAG, "User is already logged in: " + currentUser.getUid());
        } else {
            Log.d(TAG, "No current user logged in.");
        }
    }


    public interface OtpVerificationCallback {
        void onVerificationSuccess();

        void onVerificationFailed(String errorMessage);
    }
    private void saveLoginState(boolean isLoggedIn) {
        // You can use shared preferences or any other mechanism to persist login state
        // For example using shared preferences:        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // sharedPreferences.edit().putBoolean(\"isLoggedIn\", isLoggedIn).apply();
    }
}



