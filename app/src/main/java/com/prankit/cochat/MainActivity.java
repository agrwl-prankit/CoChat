package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAccessAdapter tabsAccessAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;          // to access firebase methods
    private DatabaseReference dbReference;      // to read and write data from database we need an instance of DatabaseReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add toolBar to screen
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CoChat");

        // initialize all fields and instances
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        viewPager = findViewById(R.id.main_tabs_pager);
        tabsAccessAdapter = new TabsAccessAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessAdapter);
        tabLayout = findViewById(R.id.tabBar);
        tabLayout.setupWithViewPager(viewPager);
    }

    // when app will start this method is called
    @Override
    protected void onStart() {
        super.onStart();
        // check if user is already login or not
        if (currentUser == null){
            sendUserToLoginActivity();
            finish();
        }
        // check whether the profile (username and status) is updated or not
        else {
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();  // get current user (login user) from firebase
        // read data from firebaseDatabase and listen for changes
        dbReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("name").exists()){       // check whether the name exists or not
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                }
                else {      // if not then send user to settingsActivity to update profile
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // add option menu from optionMenu.xml in MainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu, menu);
        return true;
    }

    // when items from menuBar is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logoutOption){
            firebaseAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.settingsOption){
            sendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.findFriendOption){

        }
        return true;
    }

    private void sendUserToSettingsActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}