package com.prankit.cochat.fragments;

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
import com.prankit.cochat.activities.UserProfileActivity;
import com.prankit.cochat.model.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private DatabaseReference requestRef, userRef, contactsRef;
    private String currentUserId;
    private RecyclerView requestList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        requestList = requestFragmentView.findViewById(R.id.friendRequestListView);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(requestRef.child(currentUserId), Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, final int position, @NonNull Contact model) {
                final String listUserId = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            if (type.equals("received")) {
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")) {
                                            final String requestUserImage = snapshot.child("image").getValue().toString();
                                            final String requestUserName = snapshot.child("name").getValue().toString();
                                            holder.requestName.setText(requestUserName);
                                            holder.requestStatus.setText("Wants to connect with you");
                                            Glide.with(RequestFragment.this).load(requestUserImage)
                                                    .placeholder(R.drawable.profileimage).into(holder.requestImage);
                                        }
                                        final String requestUserName = snapshot.child("name").getValue().toString();
                                        holder.requestName.setText(requestUserName);
                                        holder.requestStatus.setText("Wants to connect with you");
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
                            else if (type.equals("sent")){
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")) {
                                            final String requestUserImage = snapshot.child("image").getValue().toString();
                                            Glide.with(RequestFragment.this).load(requestUserImage)
                                                    .placeholder(R.drawable.profileimage).into(holder.requestImage);
                                        }
                                        final String requestUserName = snapshot.child("name").getValue().toString();
                                        holder.requestName.setText(requestUserName);
                                        holder.requestStatus.setText("Did not accept your friend request yet");
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplayview, parent, false);
                RequestViewHolder viewHolder = new RequestViewHolder(view);
                return viewHolder;
            }
        };
        requestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView requestName, requestStatus;
        CircleImageView requestImage;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestName = itemView.findViewById(R.id.userProfileNameView);
            requestStatus = itemView.findViewById(R.id.userStatusView);
            requestImage = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}