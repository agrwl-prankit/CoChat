package com.prankit.cochat.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prankit.cochat.R;
import com.prankit.cochat.adapter.FriendRequestAdapter;
import com.prankit.cochat.model.Contact;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private DatabaseReference requestRef, userRef, contactsRef;
    private String currentUserId;
    private RecyclerView requestList;
    private List<Contact> arrayList;
    private FriendRequestAdapter adapter;

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

        /*FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
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
                        if (snapshot.exists() && snapshot.getValue().toString().equals("received")) {
                            userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild("image")){
                                        final String requestUserImage = snapshot.child("image").getValue().toString();
                                        final String requestUserName = snapshot.child("name").getValue().toString();
                                        holder.userName.setText(requestUserName);
                                        holder.userStatus.setText("Wants to connect with you");
                                        Glide.with(RequestFragment.this).load(requestUserImage)
                                                .placeholder(R.drawable.profileimage).into(holder.userProfile);
                                    }
                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                    holder.userName.setText(requestUserName);
                                    holder.userStatus.setText("Wants to connect with you");
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
        adapter.startListening();*/

        requestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                for (DataSnapshot s1 : snapshot.getChildren()) {
                    if (s1.child("request_type").getValue().toString().equals("received")) {
                        final String id = s1.getKey();
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                arrayList = new ArrayList<Contact>();
                                for (DataSnapshot s1 : snapshot1.getChildren()) {
                                    //Contact obj = s1.getValue(Contact.class);
                                    //arrayList.add(obj);
                                    if (s1.getKey().equals(id)){
                                        Contact obj = s1.getValue(Contact.class);
                                        arrayList.add(obj);
                                    }
                                }
                                Log.i("set", arrayList.toString());
                                adapter = new FriendRequestAdapter(arrayList, getContext());
                                requestList.setAdapter(adapter);
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
}