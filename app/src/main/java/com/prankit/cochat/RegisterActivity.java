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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText emailEditText, passwordEditText;
    private TextView alreadyHaveAccountTextView;
    private FirebaseAuth firebaseAuth;          // to access firebase methods
    private ProgressDialog loadingBar;
    private DatabaseReference dbReferences;     // to read and write data from database we need an instance of DatabaseReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize all fields and instances
        firebaseAuth = FirebaseAuth.getInstance();
        dbReferences = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        createAccountButton = findViewById(R.id.registerButton);
        emailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        alreadyHaveAccountTextView = findViewById(R.id.alreadyHaveAccountTextView);

        // when alreadyHaveAccount text is clicked
        alreadyHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });

        // when createNewAccount button is clicked
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
        //  check whether users enters email and password or not
        if (email.equals(""))
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        else if (password.equals(""))
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        //  if both have entered
        else {
            //  add loading bar to indicate the user
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait! while we are creating new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            //  send email and password to firebase for creating new user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                //  add value of current user id to firebase database also
                                dbReferences.child("Users").child(currentUserId).setValue("");
                                Toast.makeText(RegisterActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                            }
                            else {
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Failed to create user")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    //  send user to login activity
    public void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    //  send user to main activity
    public void sendUserToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}