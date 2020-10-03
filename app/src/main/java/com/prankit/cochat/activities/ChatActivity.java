package com.prankit.cochat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prankit.cochat.R;
import com.prankit.cochat.adapter.MessageAdapter;
import com.prankit.cochat.model.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId, messageSenderId, messageReceiverName, messageRecieverImage;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private EditText inputMessage;
    private ImageButton sendButton;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar chatToolBar = findViewById(R.id.chatToolBar);
        setSupportActionBar(chatToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        getSupportActionBar().setCustomView(actionBarView);

        messageReceiverId = getIntent().getExtras().get("chatUserId").toString();
        messageReceiverName = getIntent().getExtras().get("chatUserName").toString();
        messageRecieverImage = getIntent().getExtras().get("chatUserImage").toString();
        userName = findViewById(R.id.chatUserName);
        userLastSeen = findViewById(R.id.chatUserLastSeen);
        userImage = findViewById(R.id.chatUserImage);
        userName.setText(messageReceiverName);
        Glide.with(this).load(messageRecieverImage).placeholder(R.drawable.profileimage).into(userImage);

        inputMessage = findViewById(R.id.inputChatMessage);
        sendButton = findViewById(R.id.sendChatMessageButton);
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        messageAdapter = new MessageAdapter(messagesList, getApplicationContext());
        userMessageList = findViewById(R.id.chatMessageRecyclerList);
        layoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(layoutManager);
        userMessageList.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void sendMessage() {
        String messageText = inputMessage.getText().toString();
        if (messageText.equals("")){
            Toast.makeText(this, "Please write your message..", Toast.LENGTH_SHORT).show();
        }
        else {
            String messageSenderRef = "Message/" + messageSenderId + "/" + messageReceiverId;
            String messageReceiverRef = "Message/" + messageReceiverId + "/" + messageSenderId;
            DatabaseReference messageKeyRef = rootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();
            String messagePushId = messageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){

                    }
                    inputMessage.setText("");
                }
            });
        }
    }
}