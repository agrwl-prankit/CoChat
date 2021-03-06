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
import com.prankit.cochat.activities.FindFriendsActivity;
import com.prankit.cochat.activities.UserProfileActivity;
import com.prankit.cochat.model.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView contactsListView;
    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ProgressDialog loadingBar;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsListView = contactsView.findViewById(R.id.contactRecyclerList);
        contactsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingBar = new ProgressDialog(getActivity());

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadingBar.setTitle("Retrieving Contacts");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(contactsRef, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ContactsViewHolder> adapter = new
                FirebaseRecyclerAdapter<Contact, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull Contact model) {
                String userId = getRef(position).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChild("image")){
                            String userImage = snapshot.child("image").getValue().toString();
                            String profileName = snapshot.child("name").getValue().toString();
                            String profileStatus = snapshot.child("status").getValue().toString();
                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                            Glide.with(ContactsFragment.this).load(userImage)
                                    .placeholder(R.drawable.profileimage).into(holder.profileImage);
                        }
                        else if (snapshot.exists()){
                            String profileName = snapshot.child("name").getValue().toString();
                            String profileStatus = snapshot.child("status").getValue().toString();
                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                        }
                        else {
                            Toast.makeText(getContext(), "No contact is available.", Toast.LENGTH_SHORT).show();
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visitUserId = getRef(position).getKey();
                                Intent profileIntent = new Intent(getContext(), UserProfileActivity.class);
                                profileIntent.putExtra("visituserId", visitUserId);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplayview, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        contactsListView.setAdapter(adapter);
        adapter.startListening();
        loadingBar.dismiss();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userProfileNameView);
            userStatus = itemView.findViewById(R.id.userStatusView);
            profileImage = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}