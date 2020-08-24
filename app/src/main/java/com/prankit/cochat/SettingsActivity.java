package com.prankit.cochat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private  EditText userNameEditText, statusEditText;
    private  Button updateSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userNameEditText = (EditText) findViewById(R.id.setUserName);
        statusEditText = (EditText) findViewById(R.id.setStatus);
        updateSettingButton = (Button) findViewById(R.id.updateSettingButton);
    }
}