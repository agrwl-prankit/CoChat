package com.prankit.cochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private Button loginButton, phoneLoginButton;
    private EditText emailEdiText, passwordEditText;
    private TextView needNewAccountTextView, forgetPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFields();
        needNewAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });
    }

    private void initializeFields() {
        loginButton = (Button) findViewById(R.id.loginButton);
        phoneLoginButton = (Button) findViewById(R.id.phoneLoginButton);
        emailEdiText = (EditText) findViewById(R.id.loginEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);
        needNewAccountTextView = (TextView) findViewById(R.id.needNewAccountTextView);
        forgetPasswordTextView= (TextView) findViewById(R.id.forgetPasswordTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null){
            sendUserToMainActivity();
        }
    }

    public void sendUserToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void sendUserToRegisterActivity(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}