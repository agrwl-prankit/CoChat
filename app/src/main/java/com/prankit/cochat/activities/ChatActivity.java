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

    private String messageRecieverId, messageRecieverName, messageRecieverImage;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolBar = findViewById(R.id.chatToolBar);
        setSupportActionBar(chatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        messageRecieverId = getIntent().getExtras().get("chatUserId").toString();
        messageRecieverName = getIntent().getExtras().get("chatUserName").toString();
        messageRecieverImage = getIntent().getExtras().get("chatUserImage").toString();
        userName = findViewById(R.id.chatUserName);
        userLastSeen = findViewById(R.id.chatUserLastSeen);
        userImage = findViewById(R.id.chatUserImage);
        userName.setText(messageRecieverName);
        Glide.with(this).load(messageRecieverImage).placeholder(R.drawable.profileimage).into(userImage);
    }
}