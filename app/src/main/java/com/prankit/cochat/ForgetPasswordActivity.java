package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText forgetPasswordEditText;
    private Button sendForgetPasswordLinkButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        forgetPasswordEditText = findViewById(R.id.forgetPasswordEditText);
        sendForgetPasswordLinkButton = findViewById(R.id.sendForgetPasswordLinkButton);
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        sendForgetPasswordLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLinkToEmail();
            }
        });
    }

    private void sendLinkToEmail() {
        String inputEmail = forgetPasswordEditText.getText().toString();
        if (inputEmail.equals(""))
            Toast.makeText(this, "Please enter email first", Toast.LENGTH_SHORT).show();
        else {
            loadingBar.setTitle("Sending reset link");
            loadingBar.setMessage("Please wait! while we are sending reset link");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            firebaseAuth.sendPasswordResetEmail(inputEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "reset email sent", Toast.LENGTH_SHORT).show();
                                sendUserToLoginActivity();
                            }
                            else {

                                new AlertDialog.Builder(ForgetPasswordActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Failed to send reset link")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                sendUserToLoginActivity();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
        }
    }

    private void sendUserToLoginActivity() {
        Intent mainIntent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}