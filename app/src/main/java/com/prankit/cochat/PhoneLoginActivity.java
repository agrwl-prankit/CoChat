package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button verifyCodeButton, sendCodeButton;
    private EditText inputPhoneNumber, inputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        verifyCodeButton = findViewById(R.id.verifyCodeButton);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        inputPhoneNumber = findViewById(R.id.phoneNumberEditText);
        inputVerificationCode = findViewById(R.id.verificationCodeEditText);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputPhoneNumber.getText().toString().equals("")){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter phone number..", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingbar.setTitle("Phone verification");
                    loadingbar.setMessage("Please wait, we are authenticating your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            inputPhoneNumber.getText().toString(),        // Phone number to verify
                            60,                                        // Timeout duration
                            TimeUnit.SECONDS,                             // Unit of timeout
                            PhoneLoginActivity.this,              // Activity (for callback binding)
                            mCallbacks);                                  // OnVerificationStateChangedCallbacks
                }
            }
        });

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCodeButton.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                if (inputVerificationCode.getText().toString().equals("")) {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingbar.setTitle("Verification code");
                    loadingbar.setMessage("Please wait, we are verifying your verification code");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, inputVerificationCode.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingbar.dismiss();
                new AlertDialog.Builder(PhoneLoginActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Invalid phone number")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();

                sendCodeButton.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);
                verifyCodeButton.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loadingbar.dismiss();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
                sendCodeButton.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                verifyCodeButton.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Yoy are logged in successfully", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }
                        else {
                            new AlertDialog.Builder(PhoneLoginActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Error in login")
                                    .setMessage(task.getException().getMessage())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}