package com.prankit.cochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar groupChatToolbar;
    private ImageButton sendGroupMessageButton;
    private EditText inputGroupMessage;
    private RecyclerView groupMessageList;
    private String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference, groupNameReference, groupMessageKeyRef;
    private int adapterCount;

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
                //groupMessageList.smoothScrollToPosition(adapterCount-1);
            }
        });

        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<GroupChatInfo> options = new FirebaseRecyclerOptions.Builder<GroupChatInfo>()
                .setQuery(groupNameReference, GroupChatInfo.class)
                .build();

        FirebaseRecyclerAdapter<GroupChatInfo, GroupChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<GroupChatInfo, GroupChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position, @NonNull GroupChatInfo model) {
                holder.name.setText(model.getName());
                holder.message.setText(model.getMessage());
                holder.time.setText(model.getTime());
                holder.date.setText(model.getDate());
            }

            @NonNull
            @Override
            public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_message_view, viewGroup, false);
                GroupChatViewHolder viewHolder = new GroupChatViewHolder(view);
                return viewHolder;
            }
        };
        groupMessageList.setAdapter(adapter);
        adapter.startListening();
        adapterCount = adapter.getItemCount();
        //groupMessageList.smoothScrollToPosition(adapter.getItemCount()-1);
    }

    public static class GroupChatViewHolder extends RecyclerView.ViewHolder{
        TextView name, message, time, date;
        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groupUserNameView);
            message = itemView.findViewById(R.id.groupMessageView);
            time = itemView.findViewById(R.id.groupTimeView);
            date = itemView.findViewById(R.id.groupDateView);
        }
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