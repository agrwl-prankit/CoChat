package com.prankit.cochat.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prankit.cochat.R;
import com.prankit.cochat.activities.ChatActivity;
import com.prankit.cochat.model.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private RecyclerView chatList;
    private DatabaseReference chatRef, userRef;
    private  FirebaseAuth mAuth;
    private String currentUserId;
    private ProgressDialog loadingBar;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View chatView = inflater.inflate(R.layout.fragment_chat, container, false);

        loadingBar = new ProgressDialog(getActivity());
        chatList = chatView.findViewById(R.id.chatList);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return chatView;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingBar.setTitle("Retrieving Contacts");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(chatRef.child(currentUserId), Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ChatViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contact model) {
                final String userId = getRef(position).getKey();
                final String[] userImage = {"default_image"};
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("image")) {
                                userImage[0] = snapshot.child("image").getValue().toString();
                                Glide.with(ChatFragment.this).load(userImage[0])
                                        .placeholder(R.drawable.profileimage).into(holder.profileImage);
                            }
                            final String profileName = snapshot.child("name").getValue().toString();
                            holder.userName.setText(profileName);
                            holder.userStatus.setVisibility(View.INVISIBLE);
                            //holder.userStatus.setText("Last seen: " + "\n" + "Date " + "Time ");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("chatUserId", userId);
                                    chatIntent.putExtra("chatUserName", profileName);
                                    chatIntent.putExtra("chatUserImage", userImage[0]);
                                    startActivity(chatIntent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplayview, parent, false);
                ChatViewHolder viewHolder = new ChatViewHolder(view);
                return viewHolder;
            }
        };
        chatList.setAdapter(adapter);
        adapter.startListening();
        loadingBar.dismiss();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userProfileNameView);
            userStatus = itemView.findViewById(R.id.userStatusView);
            profileImage = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}