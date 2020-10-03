package com.prankit.cochat.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.prankit.cochat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId, messageReceiverName, messageRecieverImage;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;

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
    }
}