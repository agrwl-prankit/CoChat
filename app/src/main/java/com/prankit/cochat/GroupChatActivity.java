package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar groupChatToolbar;
    private ImageButton sendGroupMessageButton;
    private EditText inputGroupMessage;
    private ScrollView scrollView;
    private TextView displayTextMessage;
    private String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference, groupNameReference, groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        groupChatToolbar = findViewById(R.id.groupChatToolBar);
        setSupportActionBar(groupChatToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendGroupMessageButton = findViewById(R.id.sendGroupMessageButton);
        inputGroupMessage = findViewById(R.id.inputGroupMessage);
        scrollView = findViewById(R.id.groupChatScrollView);
        displayTextMessage = findViewById(R.id.groupChatDisplay);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        sendGroupMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageInfoToDatabse();
                inputGroupMessage.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayMessages(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            displayTextMessage.append(chatName + ":\n" + chatMessage + "\n" + chatTime + "      " + chatDate);
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void saveMessageInfoToDatabse() {
        String message = inputGroupMessage.getText().toString();
        String messageKey = groupNameReference.push().getKey();
        if (message.equals(""))
            Toast.makeText(this, "Please input the message first..", Toast.LENGTH_SHORT).show();
        else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MM, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameReference.updateChildren(groupMessageKey);
            groupMessageKeyRef = groupNameReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void getUserInfo() {
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUserName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}