package com.prankit.cochat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar groupChatToolbar;
    private ImageButton sendGroupMessageButton;
    private EditText inputGroupMessage;
    private ScrollView scrollView;
    private TextView displayTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupChatToolbar = findViewById(R.id.groupChatToolBar);
        setSupportActionBar(groupChatToolbar);
        getSupportActionBar().setTitle("Group Name");

        sendGroupMessageButton = findViewById(R.id.sendGroupMessageButton);
        inputGroupMessage = findViewById(R.id.inputGroupMessage);
        scrollView = findViewById(R.id.groupChatScrollView);
        displayTextMessage = findViewById(R.id.groupChatDisplay);
    }
}