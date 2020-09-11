package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private EditText userNameEditText, statusEditText;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private DatabaseReference dbReference;
    private static final int GalleryPickRequest = 1;
    private StorageReference userProfileImageReference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar settingToolbar = findViewById(R.id.settingToolBar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setTitle("Settings");

        userNameEditText = findViewById(R.id.setUserName);
        statusEditText = findViewById(R.id.setStatus);
        Button updateSettingButton = findViewById(R.id.updateSettingButton);
        userProfileImage = findViewById(R.id.profileImage);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        updateSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/'");
                startActivityForResult(galleryIntent, GalleryPickRequest);
            }
        });

        retrieveUserInfo();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPickRequest && resultCode == RESULT_OK && data != null){
            //Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPickRequest && resultCode == RESULT_OK){
            loadingBar.setMessage("Please wait, profile image is uploading...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            Uri selectImageUri = data.getData();
            final StorageReference imageReference = userProfileImageReference.child(currentUserId + ".jpg");
            imageReference.putFile(selectImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            loadingBar.dismiss();
                            Toast.makeText(SettingsActivity.this, "profile image uploaded successfully.", Toast.LENGTH_SHORT).show();
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    dbReference.child("Users").child(currentUserId).child("image").setValue(url);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Failed to upload profile image")
                                    .setMessage(e.getMessage())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
        }
    }

    private void retrieveUserInfo() {
        dbReference.child("Users").child(currentUserId)     //  currentUserId in Users node from database
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {  // snapshot means currentUserId in Users
                        if (snapshot.exists() && snapshot.hasChild("name") && snapshot.hasChild("image")){
                            //String retrieveUserName = snapshot.child("name").getValue().toString();
                            //String retrieveUserStatus = snapshot.child("status").getValue().toString();
                            //String retrieveUserImage = snapshot.child("image").getValue().toString();
                            userNameEditText.setText(snapshot.child("name").getValue().toString());
                            statusEditText.setText(snapshot.child("status").getValue().toString());
                            Glide.with(SettingsActivity.this).load(snapshot.child("image").getValue().toString()).into(userProfileImage);
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

            dbReference.child("Users").child(currentUserId)
                    .setValue(profileMap)
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