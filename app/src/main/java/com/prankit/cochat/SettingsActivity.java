package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userNameEditText, statusEditText;
    private Button updateSettingButton;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbReference;
    private Toolbar settingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingToolbar = findViewById(R.id.settingToolBar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setTitle("Settings");

        userNameEditText = findViewById(R.id.setUserName);
        statusEditText = findViewById(R.id.setStatus);
        updateSettingButton = findViewById(R.id.updateSettingButton);
        userProfileImage = findViewById(R.id.profileImage);
        firebaseAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        updateSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        dbReference.child("Users").child(currentUserId)     //  currentUserId in Users node from database
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {  // snapshot means currentUserId in Users
                        if (snapshot.exists() && snapshot.hasChild("name") && snapshot.hasChild("image")){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveUserStatus = snapshot.child("status").getValue().toString();
                            String retrieveUserImage = snapshot.child("image").getValue().toString();
                            userNameEditText.setText(retrieveUserName);
                            statusEditText.setText(retrieveUserStatus);
                        }
                        else if (snapshot.exists() && snapshot.hasChild("name")){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveUserStatus = snapshot.child("status").getValue().toString();
                            userNameEditText.setText(retrieveUserName);
                            statusEditText.setText(retrieveUserStatus);
                        }
                        else {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Update profile")
                                    .setMessage("please update your profile. Profile image is optional but username and status is compulsory")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateSettings(){

        if (userNameEditText.getText().toString().equals(""))
            Toast.makeText(this, "Please write username!", Toast.LENGTH_SHORT).show();
        else if (statusEditText.getText().toString().equals(""))
            Toast.makeText(this, "Please write status!", Toast.LENGTH_SHORT).show();
        else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", userNameEditText.getText().toString());
            profileMap.put("status", statusEditText.getText().toString());

            dbReference.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                new AlertDialog.Builder(SettingsActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Failed to update settings")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        }
                    });
        }
    }

    public void sendUserToMainActivity(){
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendUserToMainActivity();
        super.onBackPressed();
        finish();
    }
}