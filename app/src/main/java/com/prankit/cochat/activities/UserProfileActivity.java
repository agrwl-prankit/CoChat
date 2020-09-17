package com.prankit.cochat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prankit.cochat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String recieveUserId, senderUserId, currentState;
    private TextView visitUserName, visitUserStatus;
    private CircleImageView visitUserProfileImage;
    private Button sendMessageRequestButton, declineMessageRequestButton;
    private DatabaseReference userRef, chatRequestRef, contactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        recieveUserId = getIntent().getExtras().get("visituserId").toString();
        visitUserName = findViewById(R.id.visitUserName);
        visitUserStatus = findViewById(R.id.visitUserStatus);
        visitUserProfileImage = findViewById(R.id.visitUserProfileImage);
        sendMessageRequestButton = findViewById(R.id.sendMessageRequestButton);
        declineMessageRequestButton = findViewById(R.id.declineMessageRequestButton);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        currentState = "new";

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        userRef.child(recieveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("image")){
                    String name = snapshot.child("name").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();
                    Glide.with(UserProfileActivity.this).load(image).placeholder(R.drawable.profileimage).into(visitUserProfileImage);
                    visitUserName.setText(name);
                    visitUserStatus.setText(status);
                    manageChatRequest();
                }
                else {
                    String name = snapshot.child("name").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    visitUserName.setText(name);
                    visitUserStatus.setText(status);
                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void manageChatRequest() {
        chatRequestRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(recieveUserId)){
                    String request_type = snapshot.child(recieveUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")){
                        currentState = "request_sent";
                        sendMessageRequestButton.setText("Cancel Request");
                    }
                    else if (request_type.equals("received")){
                        currentState = "request_received";
                        sendMessageRequestButton.setText("Accept Request");
                        declineMessageRequestButton.setVisibility(View.VISIBLE);
                        declineMessageRequestButton.setEnabled(true);
                        declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelChatRequest();
                            }
                        });
                    }
                }
                else {
                    contactsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(recieveUserId)){
                                currentState = "friends";
                                sendMessageRequestButton.setText("Remove");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!senderUserId.equals(recieveUserId)){
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageRequestButton.setEnabled(false);
                    if (currentState.equals("new")){
                        sendChatRequest();
                    }
                    if (currentState.equals("request_sent")){
                        cancelChatRequest();
                    }
                    if (currentState.equals("request_received")){
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends")){
                        removeFriend();
                    }
                }
            });
        }
        else
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
    }

    private void removeFriend() {
        contactsRef.child(senderUserId).child(recieveUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(recieveUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestButton.setText("Send Request");
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {
        contactsRef.child(senderUserId).child(recieveUserId).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(recieveUserId).child(senderUserId).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestRef.child(senderUserId).child(recieveUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatRequestRef.child(recieveUserId).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    sendMessageRequestButton.setText("Remove");
                                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserId).child(recieveUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRequestRef.child(recieveUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestButton.setText("Send Request");
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserId).child(recieveUserId).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatRequestRef.child(recieveUserId).child(senderUserId).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                currentState = "request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            }
                        }
                    });
                }
            }
        });
    }
}