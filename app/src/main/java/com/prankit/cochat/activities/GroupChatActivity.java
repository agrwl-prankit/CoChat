package com.prankit.cochat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prankit.cochat.adapter.GroupChatAdapter;
import com.prankit.cochat.model.Contact;
import com.prankit.cochat.model.GroupChatInfo;
import com.prankit.cochat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar groupChatToolbar;
    private ImageButton sendGroupMessageButton;
    private EditText inputGroupMessage;
    private RecyclerView groupMessageList;
    private String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference, groupNameReference, groupMessageKeyRef;
    private List<GroupChatInfo> arrayList;
    private GroupChatAdapter adapter;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        groupChatToolbar = findViewById(R.id.groupChatToolBar);
        setSupportActionBar(groupChatToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mScrollView = findViewById(R.id.groupChatScrollView);
        sendGroupMessageButton = findViewById(R.id.sendGroupMessageButton);
        inputGroupMessage = findViewById(R.id.inputGroupMessage);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        groupMessageList = findViewById(R.id.groupMessageList);
        groupMessageList.setLayoutManager(new LinearLayoutManager(this));

        sendGroupMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageInfoToDatabase();
                inputGroupMessage.setText("");
                groupMessageList.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        });

        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<GroupChatInfo>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    GroupChatInfo obj = snapshot1.getValue(GroupChatInfo.class);
                    arrayList.add(obj);
                }
                adapter = new GroupChatAdapter(GroupChatActivity.this, arrayList);
                groupMessageList.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                groupMessageList.smoothScrollToPosition(adapter.getItemCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void saveMessageInfoToDatabase() {
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
}