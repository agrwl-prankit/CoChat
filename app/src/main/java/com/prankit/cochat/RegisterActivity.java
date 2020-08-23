package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText emailEditText, passwordEditText;
    private TextView alreadyHaveAccountTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        createAccountButton = (Button) findViewById(R.id.registerButton);
        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
        alreadyHaveAccountTextView = (TextView) findViewById(R.id.alreadyHaveAccountTextView);

        alreadyHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    public void createNewAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email))
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(password))
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();

        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait! while we are creating new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                sendUserToLoginActivity();
                            }
                            else{
                                String messageError = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error : " + messageError , Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    public void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}