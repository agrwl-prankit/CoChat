package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
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
        mainToolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(mainToolbar);
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
                if (!snapshot.child("name").exists()){       // check whether the name exists or not
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
        if(item.getItemId() == R.id.createGroupOption){
            requestNewGroup();
        }
        if (item.getItemId() == R.id.findFriendOption){

        }
        return true;
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter group name: ");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("enter group name...");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                if (groupName.equals(""))
                    Toast.makeText(MainActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                else {
                    createNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(final String groupName) {
        dbReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName + " is created successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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