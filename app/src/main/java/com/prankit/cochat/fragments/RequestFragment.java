package com.prankit.cochat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.prankit.cochat.model.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView requestList;
    private Button acceptRequestButton, cancelRequestButton;
    private DatabaseReference requestRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
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

        FirebaseRecyclerAdapter<Contact, RequestViewHolder> adapter = new
                FirebaseRecyclerAdapter<Contact, RequestViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contact model) {
                        holder.itemView.findViewById(R.id.requestAcceptButton).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.requestCancelButton).setVisibility(View.VISIBLE);
                        final String listUserId = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String type = snapshot.getValue().toString();
                                    if (type.equals("received")){
                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild("image")){
                                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = snapshot.child("status").getValue().toString();
                                                    final String requestUserImage = snapshot.child("image").getValue().toString();
                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);
                                                    Glide.with(RequestFragment.this).load(requestUserImage)
                                                            .placeholder(R.drawable.profileimage).into(holder.userProfile);
                                                }
                                                else {
                                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = snapshot.child("status").getValue().toString();
                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);
                                                }
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
        TextView userName, userStatus;
        CircleImageView userProfile;
        Button acceptButton, cancelButton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userProfileNameView);
            userStatus = itemView.findViewById(R.id.userStatusView);
            userProfile = itemView.findViewById(R.id.userProfileImageView);
            acceptButton = itemView.findViewById(R.id.requestAcceptButton);
            cancelButton = itemView.findViewById(R.id.requestCancelButton);
        }
    }
}