package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, phoneLoginButton;
    private EditText emailEditText, passwordEditText;
    private TextView needNewAccountTextView, forgetPasswordTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize fields and instances
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        loginButton = findViewById(R.id.loginButton);
        phoneLoginButton = findViewById(R.id.phoneLoginButton);
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        needNewAccountTextView = findViewById(R.id.needNewAccountTextView);
        forgetPasswordTextView= findViewById(R.id.forgetPasswordTextView);

        // when needNewAccount text is clicked
        needNewAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        // when user will click login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserToLogin();
            }
        });
    }

    public void allowUserToLogin(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        //  check whether users enters email and password or not
        if (email.equals(""))
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        else if (password.equals(""))
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        //  if both have entered
        else {
            //  add loading bar to indicate the user
            loadingBar.setTitle("SignIn");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            //  send email and password to firebase for sign in
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, task.getException().toString() , Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    //  send user to main activity
    public void sendUserToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    // send user to register activity for sign up
    public void sendUserToRegisterActivity(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}