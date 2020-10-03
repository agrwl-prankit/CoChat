package com.prankit.cochat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.prankit.cochat.R;
import com.prankit.cochat.model.Contact;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private List<Contact> list;
    private Context context;

    public FriendRequestAdapter(List<Contact> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.userdisplayview, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        holder.requestName.setText(list.get(position).getName());
        holder.requestStatus.setText("Wants to connect with you.");
        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.profileimage).into(holder.requestImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder{
        TextView requestName, requestStatus;
        CircleImageView requestImage;
        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestName = itemView.findViewById(R.id.userProfileNameView);
            requestStatus = itemView.findViewById(R.id.userStatusView);
            requestImage = itemView.findViewById(R.id.userProfileImageView);
        }
    }

}
